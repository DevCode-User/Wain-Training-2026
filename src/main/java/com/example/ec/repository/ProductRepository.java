package com.example.ec.repository;

import com.example.ec.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    List<Product> findByIsActiveTrue();

    @Query("SELECT p FROM Product p LEFT JOIN p.category c WHERE p.isActive = true AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.productName ASC")
    List<Product> searchActiveByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p LEFT JOIN p.category c WHERE p.isActive = true AND c.categoryName = :categoryName ORDER BY p.productName ASC")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.productName ASC")
    List<Product> searchByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    @Query("SELECT DISTINCT c.categoryName FROM Product p JOIN p.category c WHERE p.isActive = true ORDER BY c.categoryName ASC")
    List<String> findAllCategoryNames();

    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.productId DESC")
    List<Product> findRecommendedProducts(Pageable pageable);

    List<Product> findAllByOrderByProductIdDesc();
}
