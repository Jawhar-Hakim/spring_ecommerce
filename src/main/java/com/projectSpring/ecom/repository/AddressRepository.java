package com.projectSpring.ecom.repository;

import com.projectSpring.ecom.entity.Address;
import com.projectSpring.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUser(User user);

    Optional<Address> findByUserAndIsPrimaryTrue(User user);
}
