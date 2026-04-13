package com.example.ec.repository;

import com.example.ec.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByCustomerEmail(String customerEmail);
}
