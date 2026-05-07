package com.projectSpring.ecom.service;

import com.projectSpring.ecom.entity.Cart;
import com.projectSpring.ecom.entity.CartItem;
import com.projectSpring.ecom.entity.Product;
import com.projectSpring.ecom.entity.User;
import com.projectSpring.ecom.repository.CartRepository;
import com.projectSpring.ecom.repository.CartItemRepository;
import com.projectSpring.ecom.repository.ProductRepository;
import com.projectSpring.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    public Cart getCartForUser(Long userId) {
        return cartRepository.findByCustomerId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart cart = Cart.builder().customer(user).build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartForUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }

        cart.setLastModified(System.currentTimeMillis());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getCartForUser(userId);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cart.setLastModified(System.currentTimeMillis());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart applyCoupon(Long userId, String couponCode) {
        Cart cart = getCartForUser(userId);
        com.projectSpring.ecom.entity.Coupon coupon = couponService.validateCoupon(couponCode);
        cart.setCoupon(coupon);
        cart.setLastModified(System.currentTimeMillis());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeCoupon(Long userId) {
        Cart cart = getCartForUser(userId);
        cart.setCoupon(null);
        cart.setLastModified(System.currentTimeMillis());
        return cartRepository.save(cart);
    }
}
