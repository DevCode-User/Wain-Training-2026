package com.example.system.customer.repository;

import com.example.system.customer.service.OrderService;
import java.util.List;

/**
 * 注文リポジトリ
 */
public class OrderRepository {
    
    /**
     * 注文を作成
     */
    public OrderService.Order createOrder(String customerId, String customerName, List<OrderService.OrderItem> items, int totalAmount) {
        return OrderService.createOrder(customerId, customerName, items, totalAmount);
    }
    
    /**
     * IDで注文を取得
     */
    public OrderService.Order findById(String orderId) {
        return OrderService.getOrderById(orderId);
    }
    
    /**
     * 顧客IDで注文一覧を取得
     */
    public List<OrderService.Order> findByCustomerId(String customerId) {
        return OrderService.getOrdersByCustomerId(customerId);
    }
    
    /**
     * 全注文を取得
     */
    public List<OrderService.Order> findAll() {
        return OrderService.getAllOrders();
    }
    
    /**
     * 注文ステータスを更新
     */
    public boolean updateStatus(String orderId, String status) {
        return OrderService.updateOrderStatus(orderId, status);
    }
}
