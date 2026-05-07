package com.projectSpring.ecom.dto;

import com.projectSpring.ecom.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private UserResponse customer;
    private Order.OrderStatus status;
    private Double subTotal;
    private Double discountAmount;
    private Double shippingCost;
    private Double totalAmount;
    private Long orderDate;
    private List<OrderItemResponse> items;
}
