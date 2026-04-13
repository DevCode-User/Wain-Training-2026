package com.example.ec.service;

import com.example.ec.entity.Product;
import com.example.ec.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<Product> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByProductNameContainingAndIsActiveTrueOrderByProductNameAsc(keyword.trim());
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByCategoryAndIsActiveTrueOrderByProductNameAsc(category.trim());
    }

    public List<Product> searchByPriceRange(Integer minPrice, Integer maxPrice) {
        if (minPrice == null) minPrice = 0;
        if (maxPrice == null) maxPrice = Integer.MAX_VALUE;
        return productRepository.searchByPriceRange(minPrice, maxPrice);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public List<Product> getRecommendedProducts() {
        return productRepository.findRecommendedProducts();
    }
}

