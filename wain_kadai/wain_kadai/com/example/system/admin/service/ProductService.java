package com.example.system.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品サービス
 */
public class ProductService {
    
    private static Map<Integer, Product> productDatabase = new HashMap<>();
    
    static {
        // サンプル商品を初期化
        initializeProducts();
    }
    
    /**
     * サンプル商品を初期化
     */
    private static void initializeProducts() {
        productDatabase.put(1, new Product(1, "商品A", "高品質な商品A", 1000, 50));
        productDatabase.put(2, new Product(2, "商品B", "おすすめの商品B", 2500, 30));
        productDatabase.put(3, new Product(3, "商品C", "人気の商品C", 1800, 45));
        productDatabase.put(4, new Product(4, "商品D", "限定商品D", 3200, 15));
        productDatabase.put(5, new Product(5, "商品E", "新商品E", 2100, 60));
        productDatabase.put(6, new Product(6, "商品F", "セール中の商品F", 900, 100));
    }
    
    /**
     * 全商品を取得
     */
    public static List<Product> getAllProducts() {
        return new ArrayList<>(productDatabase.values());
    }
    
    /**
     * IDで商品を取得
     */
    public static Product getProductById(int productId) {
        return productDatabase.get(productId);
    }
    
    /**
     * 商品を追加
     */
    public static boolean addProduct(Product product) {
        if (productDatabase.containsKey(product.getId())) {
            return false;
        }
        productDatabase.put(product.getId(), product);
        return true;
    }
    
    /**
     * 商品を更新
     */
    public static boolean updateProduct(Product product) {
        if (!productDatabase.containsKey(product.getId())) {
            return false;
        }
        productDatabase.put(product.getId(), product);
        return true;
    }
    
    /**
     * 商品を削除
     */
    public static boolean deleteProduct(int productId) {
        return productDatabase.remove(productId) != null;
    }
    
    /**
     * 商品クラス
     */
    public static class Product {
        private int id;
        private String name;
        private String description;
        private int price;
        private int stock;
        
        public Product() {
        }
        
        public Product(int id, String name, String description, int price, int stock) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stock = stock;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
        
        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", stock=" + stock +
                    '}';
        }
    }
}
