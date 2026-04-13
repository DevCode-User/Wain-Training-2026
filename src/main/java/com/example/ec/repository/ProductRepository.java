package com.example.ec.repository;

import com.example.ec.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIsActiveTrue();

    List<Product> findByProductNameContainingAndIsActiveTrueOrderByProductNameAsc(String keyword);

    List<Product> findByCategoryAndIsActiveTrueOrderByProductNameAsc(String category);

    List<Product> findByPriceBetweenAndIsActiveTrueOrderByPriceAsc(Integer minPrice, Integer maxPrice);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price >= :minPrice AND p.price <= :maxPrice ORDER BY p.productName ASC")
    List<Product> searchByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true ORDER BY p.category ASC")
    List<String> findAllCategories();

    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.productId DESC")
    List<Product> findRecommendedProductsAll();
    
    default List<Product> findRecommendedProducts() {
        List<Product> all = findRecommendedProductsAll();
        return all.size() > 6 ? all.subList(0, 6) : all;
    }
}

