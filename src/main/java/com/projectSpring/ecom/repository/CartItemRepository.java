package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Cart;
import com.projectSpring.ecom.entity.CartItem;
import com.projectSpring.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProductAndVariantIsNull(Cart cart, Product product);
}
