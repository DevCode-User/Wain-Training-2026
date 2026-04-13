package com.example.ec.service;

import com.example.ec.dto.CartItem;
import com.example.ec.entity.Order;
import com.example.ec.entity.OrderItem;
import com.example.ec.entity.Product;
import com.example.ec.repository.OrderItemRepository;
import com.example.ec.repository.OrderRepository;
import com.example.ec.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Integer customerId, List<CartItem> cartItems,
                            String shippingAddress, String paymentMethod) {
        // 注文作成
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 合計金額計算と在庫確認
        Integer totalAmount = 0;
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("在庫不足または商品が見つかりません: " + item.getProductName());
            }
            totalAmount += item.getSubtotal();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // 注文アイテム作成 & 在庫減少
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getOrderId());
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getPrice());
            orderItemRepository.save(orderItem);

            // 在庫を減少
            Product product = productRepository.findById(item.getProductId()).get();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        return savedOrder;
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<Order> getCustomerOrders(Integer customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }
}
