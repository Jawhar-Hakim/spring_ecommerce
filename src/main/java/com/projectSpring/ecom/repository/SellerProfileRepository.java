package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.SellerProfile;
import com.projectSpring.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {

    Optional<SellerProfile> findByUser(User user);

    boolean existsByUser(User user);
}
