package com.example.ec.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "size", length = 100)
    private String size;

    @Column(name = "weight", length = 100)
    private String weight;

    @Column(name = "origin", length = 100)
    private String origin;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Category getCategoryEntity() { return category; }
    public void setCategoryEntity(Category category) { this.category = category; }
    public String getCategoryName() { return category != null ? category.getCategoryName() : ""; }
    public String getCategory() { return getCategoryName(); }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStockQuantity() { return stock; }
    public void setStockQuantity(Integer stock) { this.stock = stock; }
    public Boolean getIsActive() { return isActive; }
    public boolean isActive() { return Boolean.TRUE.equals(isActive); }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
}
