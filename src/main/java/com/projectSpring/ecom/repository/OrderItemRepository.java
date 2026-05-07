package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Order;
import com.projectSpring.ecom.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}
