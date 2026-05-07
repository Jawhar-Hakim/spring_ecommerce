package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Order;
import com.projectSpring.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(User customer);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(Long customerId);


    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.seller.id = :sellerId")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.customer.id = :customerId AND i.product.id = :productId")
    boolean hasPurchasedProduct(@Param("customerId") Long customerId, @Param("productId") Long productId);
}
