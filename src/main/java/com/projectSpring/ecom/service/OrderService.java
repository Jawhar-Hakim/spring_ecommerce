package com.projectSpring.ecom.service;

import com.projectSpring.ecom.dto.OrderItemResponse;
import com.projectSpring.ecom.dto.OrderResponse;
import com.projectSpring.ecom.entity.*;
import com.projectSpring.ecom.mapper.UserMapper;
import com.projectSpring.ecom.repository.CartRepository;
import com.projectSpring.ecom.repository.CouponRepository;
import com.projectSpring.ecom.repository.OrderRepository;
import com.projectSpring.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final UserMapper userMapper;

    public List<OrderResponse> getOrdersBySeller(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByCustomer(User customer) {
        return orderRepository.findByCustomer(customer).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartService.getCartForUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double subTotal = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        double discountAmount = 0.0;
        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();
            if (coupon.getType() == Coupon.DiscountType.PERCENT) {
                discountAmount = subTotal * (coupon.getValue() / 100);
            } else {
                discountAmount = coupon.getValue();
            }
            // Ensure discount doesn't exceed subtotal
            discountAmount = Math.min(discountAmount, subTotal);
            
            // Update coupon usage
            coupon.setCurrentUsages(coupon.getCurrentUsages() + 1);
            couponRepository.save(coupon);
        }

        double totalAmount = subTotal - discountAmount;

        Order order = Order.builder()
                .customer(customer)
                .orderNumber(generateOrderNumber())
                .status(Order.OrderStatus.PENDING)
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .coupon(cart.getCoupon())
                .shippingCost(0.0)
                .totalAmount(totalAmount)
                .orderDate(System.currentTimeMillis())
                .items(new ArrayList<>())
                .build();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.getItems().add(orderItem);
            
            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cart.getItems().clear();
        cart.setCoupon(null);
        cartRepository.save(cart);

        return mapToResponse(savedOrder);
    }

    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(newStatus.toUpperCase()));
        return mapToResponse(orderRepository.save(order));
    }

    public String getOrderStatus(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new RuntimeException("not found"));
        return order.getStatus().toString();
    }

    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        // Restock products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customer(userMapper.toResponse(order.getCustomer()))
                .status(order.getStatus())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .shippingCost(order.getShippingCost())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .items(order.getItems().stream()
                        .map(i -> OrderItemResponse.builder()
                                .id(i.getId())
                                .productId(i.getProduct().getId())
                                .productName(i.getProduct().getName())
                                .quantity(i.getQuantity())
                                .unitPrice(i.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private String generateOrderNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String randomStr = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "ORD-" + year + "-" + randomStr;
    }
}