package com.example.ec.service;

import com.example.ec.entity.Order;
import com.example.ec.entity.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AdminService {
    private final ProductService productService;
    private final OrderService orderService;
    public AdminService(ProductService productService, OrderService orderService) { this.productService = productService; this.orderService = orderService; }
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> m = new LinkedHashMap<>();
        List<Product> products = productService.getAllProductsForAdmin();
        List<Order> orders = orderService.getAllOrders();
        long zeroStockCount = products.stream().filter(p -> p.getStock() != null && p.getStock() <= 0).count();
        m.put("productCount", products.size());
        m.put("activeProductCount", productService.countActiveProducts());
        m.put("zeroStockCount", zeroStockCount);
        m.put("orderCount", orders.size());
        m.put("recentOrders", orders.stream().limit(5).toList());
        return m;
    }
    public Map<Integer, Integer> parseStockCsv(MultipartFile file) throws IOException {
        Map<Integer, Integer> stockMap = new LinkedHashMap<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
            for (CSVRecord record : parser) {
                Integer productId = Integer.valueOf(record.get("product_id").trim());
                Integer stock = Integer.valueOf(record.get("stock").trim());
                if (stock < 0) throw new IllegalArgumentException("在庫数には0以上の数値を入力してください。");
                stockMap.put(productId, stock);
            }
        }
        return stockMap;
    }
}
