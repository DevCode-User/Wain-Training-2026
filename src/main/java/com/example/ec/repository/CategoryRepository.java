package com.example.ec.repository;

import com.example.ec.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByOrderByCategoryNameAsc();
    Optional<Category> findByCategoryName(String categoryName);
}
