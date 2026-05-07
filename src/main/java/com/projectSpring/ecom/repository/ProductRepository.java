package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Category;
import com.projectSpring.ecom.entity.Product;
import com.projectSpring.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySeller(User seller);

    List<Product> findByActiveTrue();

    List<Product> findBySellerAndActiveTrue(User seller);

    List<Product> findByCategoriesContaining(Category category);
}
