package com.example.ec.dto;

import jakarta.validation.constraints.*;

public class ProductForm {
    private Integer productId;
    @NotNull(message = "カテゴリを選択してください。")
    private Integer categoryId;
    @NotBlank(message = "商品名を入力してください。")
    @Size(max = 100, message = "商品名は100文字以内で入力してください。")
    private String productName;
    @NotBlank(message = "商品説明を入力してください。")
    @Size(max = 2000, message = "商品説明は2000文字以内で入力してください。")
    private String description;
    @NotNull(message = "価格を入力してください。")
    @Min(value = 0, message = "単価には0以上の数値を入力してください。")
    private Integer price;
    @NotNull(message = "在庫数を入力してください。")
    @Min(value = 0, message = "在庫数には0以上の数値を入力してください。")
    private Integer stock;
    private Boolean isActive = true;
    @Size(max = 255, message = "画像URLは255文字以内で入力してください。")
    private String imageUrl;
    @Size(max = 100, message = "サイズは100文字以内で入力してください。")
    private String size;
    @Size(max = 100, message = "重量は100文字以内で入力してください。")
    private String weight;
    @Size(max = 100, message = "原産地は100文字以内で入力してください。")
    private String origin;
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getIsActive() { return isActive == null ? Boolean.TRUE : isActive; }
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
