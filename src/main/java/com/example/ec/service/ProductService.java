package com.example.ec.service;

import com.example.ec.dto.ProductForm;
import com.example.ec.entity.Category;
import com.example.ec.entity.Product;
import com.example.ec.repository.CategoryRepository;
import com.example.ec.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() { return productRepository.findByIsActiveTrue(); }
    public List<Product> getAllProductsForAdmin() { return productRepository.findAllByOrderByProductIdDesc(); }
    public List<Product> searchByKeyword(String keyword) { return (keyword == null || keyword.trim().isEmpty()) ? getAllProducts() : productRepository.searchActiveByKeyword(keyword.trim()); }
    public List<Product> getProductsByCategory(String categoryName) { return (categoryName == null || categoryName.trim().isEmpty()) ? getAllProducts() : productRepository.findByCategoryName(categoryName.trim()); }
    public List<Product> searchByPriceRange(Integer minPrice, Integer maxPrice) { return productRepository.searchByPriceRange(minPrice == null ? 0 : minPrice, maxPrice == null ? Integer.MAX_VALUE : maxPrice); }
    public List<String> getAllCategoryNames() { return productRepository.findAllCategoryNames(); }
    public List<Category> getAllCategories() { return categoryRepository.findAllByOrderByCategoryNameAsc(); }
    public Product getProductById(Integer productId) { return productRepository.findById(productId).orElse(null); }
    public List<Product> getRecommendedProducts() { return productRepository.findRecommendedProducts(PageRequest.of(0, 6)); }
    public long countActiveProducts() { return productRepository.findByIsActiveTrue().size(); }

    public ProductForm toForm(Product product) {
        ProductForm form = new ProductForm();
        form.setProductId(product.getProductId());
        form.setCategoryId(product.getCategoryId());
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setStock(product.getStock());
        form.setIsActive(product.getIsActive());
        form.setImageUrl(product.getImageUrl());
        form.setSize(product.getSize());
        form.setWeight(product.getWeight());
        form.setOrigin(product.getOrigin());
        return form;
    }

    public Product saveProduct(ProductForm form) { Product p = new Product(); applyForm(p, form); return productRepository.save(p); }
    public Product updateProduct(Integer productId, ProductForm form) {
        Product p = getProductById(productId);
        if (p == null) throw new IllegalArgumentException("商品が存在しません。");
        applyForm(p, form);
        return productRepository.save(p);
    }
    public void softDeleteProduct(Integer productId) {
        Product p = getProductById(productId);
        if (p == null) throw new IllegalArgumentException("商品が存在しません。");
        p.setIsActive(false);
        productRepository.save(p);
    }
    public void bulkUpdateStocks(Map<Integer, Integer> stockMap) {
        for (Map.Entry<Integer, Integer> e : stockMap.entrySet()) {
            Product p = getProductById(e.getKey());
            if (p != null && e.getValue() != null && e.getValue() >= 0) {
                p.setStock(e.getValue());
                productRepository.save(p);
            }
        }
    }

    public Page<Product> searchForApi(String keyword, Integer categoryId, String sort, int page, int limit) {
        Sort sortSpec = buildSort(sort);
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, sortSpec);
        Specification<Product> spec = (root, query, cb) -> {
            query.distinct(true);
            List<jakarta.persistence.criteria.Predicate> preds = new ArrayList<>();
            preds.add(cb.isTrue(root.get("isActive")));
            if (keyword != null && !keyword.isBlank()) preds.add(cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
            if (categoryId != null) preds.add(cb.equal(root.get("categoryId"), categoryId));
            return cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        return productRepository.findAll(spec, pageable);
    }

    private Sort buildSort(String sort) {
        if ("price_asc".equalsIgnoreCase(sort)) return Sort.by(Sort.Direction.ASC, "price");
        if ("price_desc".equalsIgnoreCase(sort)) return Sort.by(Sort.Direction.DESC, "price");
        return Sort.by(Sort.Direction.ASC, "productId");
    }

    private void applyForm(Product product, ProductForm form) {
        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("カテゴリが存在しません。"));
        product.setCategoryId(category.getCategoryId());
        product.setCategoryEntity(category);
        product.setProductName(form.getProductName());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setStock(form.getStock());
        product.setIsActive(form.getIsActive());
        product.setImageUrl(form.getImageUrl());
        product.setSize(form.getSize());
        product.setWeight(form.getWeight());
        product.setOrigin(form.getOrigin());
    }
}
