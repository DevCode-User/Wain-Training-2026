package com.example.ec.service;

import com.example.ec.dto.ApiOrderRequest;
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
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository; this.orderItemRepository = orderItemRepository; this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Integer customerId, List<CartItem> cartItems, String shippingAddress, String paymentMethod) {
        if (cartItems == null || cartItems.isEmpty()) throw new IllegalArgumentException("カートが空です。");
        Order order = new Order();
        order.setCustomerId(customerId); order.setOrderDate(LocalDateTime.now()); order.setShippingAddress(shippingAddress); order.setPaymentMethod(paymentMethod); order.setOrderStatus("RECEIVED"); order.setCreatedAt(LocalDateTime.now()); order.setUpdatedAt(LocalDateTime.now());
        int total = 0;
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null || !product.isActive()) throw new IllegalArgumentException("ご指定の商品IDは存在するか、販売終了しています。");
            if (product.getStock() < item.getQuantity()) throw new IllegalArgumentException("申し訳ございません。決済直前に在庫がなくなりました。");
            total += item.getSubtotal();
        }
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new IllegalArgumentException("商品が見つかりません。"));
            OrderItem oi = new OrderItem();
            oi.setOrderId(saved.getOrderId()); oi.setProductId(item.getProductId()); oi.setProductName(product.getProductName()); oi.setQuantity(item.getQuantity()); oi.setUnitPrice(product.getPrice());
            orderItemRepository.save(oi);
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        return saved;
    }

    @Transactional
    public Order createOrderFromApi(Integer customerId, ApiOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) throw new IllegalArgumentException("items は1件以上必要です。");
        int recalculated = 0;
        for (ApiOrderRequest.Item item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null || !product.isActive()) throw new IllegalArgumentException("ご指定の商品IDは存在するか、販売終了しています。");
            if (item.getQuantity() > 10) throw new IllegalArgumentException("一度に購入できる数量は最大10個までです。");
            if (product.getStock() < item.getQuantity()) throw new IllegalArgumentException("申し訳ございません。決済直前に在庫がなくなりました。");
            recalculated += product.getPrice() * item.getQuantity();
        }
        if (!Integer.valueOf(recalculated).equals(request.getTotalAmount())) throw new IllegalArgumentException("totalAmount が明細の合計と一致しません。");
        Order order = new Order();
        order.setCustomerId(customerId); order.setOrderDate(LocalDateTime.now()); order.setTotalAmount(request.getTotalAmount()); order.setShippingAddress(request.getShippingAddress()); order.setPaymentMethod(request.getPaymentMethod()); order.setOrderStatus("RECEIVED"); order.setCreatedAt(LocalDateTime.now()); order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        for (ApiOrderRequest.Item item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new IllegalArgumentException("商品が見つかりません。"));
            OrderItem oi = new OrderItem(); oi.setOrderId(saved.getOrderId()); oi.setProductId(item.getProductId()); oi.setProductName(product.getProductName()); oi.setQuantity(item.getQuantity()); oi.setUnitPrice(product.getPrice());
            orderItemRepository.save(oi);
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        return saved;
    }

    public Order getOrderById(Integer orderId) { return orderRepository.findById(orderId).orElse(null); }
    public List<OrderItem> getOrderItems(Integer orderId) { return orderItemRepository.findByOrderId(orderId); }
    public List<Order> getCustomerOrders(Integer customerId) { return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId); }
    public List<Order> getAllOrders() { return orderRepository.findAllByOrderByOrderDateDesc(); }
    public void updateOrderStatus(Integer orderId, String orderStatus) { Order o = getOrderById(orderId); if (o == null) throw new IllegalArgumentException("注文が見つかりません。"); o.setOrderStatus(orderStatus); o.setUpdatedAt(LocalDateTime.now()); orderRepository.save(o); }
}
