package com.example.system.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注文サービス
 */
public class OrderService {
    
    private static Map<String, Order> orderDatabase = new HashMap<>();
    private static int orderIdCounter = 1000;
    
    /**
     * 注文を作成
     */
    public static Order createOrder(String customerId, String customerName, List<OrderItem> items, int totalAmount) {
        String orderId = "ORD-" + (++orderIdCounter);
        Order order = new Order(
            orderId,
            customerId,
            customerName,
            items,
            totalAmount,
            "確認待ち",
            System.currentTimeMillis()
        );
        orderDatabase.put(orderId, order);
        return order;
    }
    
    /**
     * IDで注文を取得
     */
    public static Order getOrderById(String orderId) {
        return orderDatabase.get(orderId);
    }
    
    /**
     * 顧客IDで注文一覧を取得
     */
    public static List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderDatabase.values()) {
            if (order.getCustomerId().equals(customerId)) {
                result.add(order);
            }
        }
        return result;
    }
    
    /**
     * 全注文を取得
     */
    public static List<Order> getAllOrders() {
        return new ArrayList<>(orderDatabase.values());
    }
    
    /**
     * 注文ステータスを更新
     */
    public static boolean updateOrderStatus(String orderId, String status) {
        Order order = orderDatabase.get(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return true;
    }
    
    /**
     * 注文クラス
     */
    public static class Order {
        private String orderId;
        private String customerId;
        private String customerName;
        private List<OrderItem> items;
        private int totalAmount;
        private String status;
        private long createdAt;
        
        public Order() {
        }
        
        public Order(String orderId, String customerId, String customerName, List<OrderItem> items, int totalAmount, String status, long createdAt) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerName = customerName;
            this.items = items;
            this.totalAmount = totalAmount;
            this.status = status;
            this.createdAt = createdAt;
        }
        
        public String getOrderId() { return orderId; }
        public String getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public List<OrderItem> getItems() { return items; }
        public int getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCreatedAt() { return createdAt; }
        
        @Override
        public String toString() {
            return "Order{" +
                    "orderId='" + orderId + '\'' +
                    ", customerId='" + customerId + '\'' +
                    ", totalAmount=" + totalAmount +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
    
    /**
     * 注文品目クラス
     */
    public static class OrderItem {
        private int productId;
        private String productName;
        private int quantity;
        private int unitPrice;
        
        public OrderItem() {
        }
        
        public OrderItem(int productId, String productName, int quantity, int unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public int getUnitPrice() { return unitPrice; }
        public int getTotal() { return quantity * unitPrice; }
        
        @Override
        public String toString() {
            return "OrderItem{" +
                    "productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    '}';
        }
    }
}
