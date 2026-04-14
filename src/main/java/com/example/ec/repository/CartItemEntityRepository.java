package com.example.ec.repository;

import com.example.ec.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemEntityRepository extends JpaRepository<CartItemEntity, Integer> {
    List<CartItemEntity> findByCartId(Integer cartId);
    Optional<CartItemEntity> findByCartIdAndProductId(Integer cartId, Integer productId);
    void deleteByCartId(Integer cartId);
}
