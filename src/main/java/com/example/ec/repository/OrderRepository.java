package com.example.ec.repository;

import com.example.ec.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Integer customerId);
}
