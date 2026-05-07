package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Product;
import com.projectSpring.ecom.entity.Review;
import com.projectSpring.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);

    List<Review> findByCustomer(User customer);

    List<Review> findByProductIdAndApprovedTrue(Long productId);

    List<Review> findByProductAndApprovedTrue(Product product);
}
