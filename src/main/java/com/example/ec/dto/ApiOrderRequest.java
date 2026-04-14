package com.example.ec.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class ApiOrderRequest {
    @NotBlank(message = "userId は必須です。")
    private String userId;
    private String orderDate;
    @NotNull(message = "totalAmount は必須です。")
    @Min(value = 0, message = "totalAmount は0以上で指定してください。")
    private Integer totalAmount;
    @NotBlank(message = "paymentMethod は必須です。")
    private String paymentMethod;
    @NotBlank(message = "shippingAddress は必須です。")
    @Size(max = 255, message = "shippingAddress は255文字以内で入力してください。")
    private String shippingAddress;
    @NotEmpty(message = "items は1件以上必要です。")
    @Valid
    private List<Item> items = new ArrayList<>();

    public static class Item {
        @NotNull(message = "productId は必須です。")
        private Integer productId;
        @NotNull(message = "quantity は必須です。")
        @Min(value = 1, message = "quantity は1以上で指定してください。")
        @Max(value = 10, message = "一度に購入できる数量は最大10個までです。")
        private Integer quantity;
        @NotNull(message = "unitPrice は必須です。")
        @Min(value = 0, message = "unitPrice は0以上で指定してください。")
        private Integer unitPrice;
        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Integer getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
