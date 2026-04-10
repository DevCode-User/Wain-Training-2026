package com.example.system.admin;

import com.example.system.admin.repository.ProductRepository;
import com.example.system.admin.service.ProductService;
import com.example.system.common.AuthMannager;
import com.example.system.common.User;
import java.util.List;

/**
 * 管理ドメイン - 管理画面の統合ビジネスロジック
 */
public class AdminDomain {
    
    private ProductRepository productRepository;
    
    public AdminDomain() {
        this.productRepository = new ProductRepository();
    }
    
    /**
     * ユーザーが管理者権限を持つかチェック
     */
    public boolean isAdmin(String token) {
        User user = AuthMannager.getUserByToken(token);
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    /**
     * 全商品を取得
     */
    public List<ProductService.Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * 商品詳細を取得
     */
    public ProductService.Product getProductDetail(int productId) {
        return productRepository.findById(productId);
    }
    
    /**
     * 商品を追加
     */
    public boolean addProduct(ProductService.Product product) {
        return productRepository.save(product);
    }
    
    /**
     * 商品を更新
     */
    public boolean updateProduct(ProductService.Product product) {
        return productRepository.update(product);
    }
    
    /**
     * 商品を削除
     */
    public boolean deleteProduct(int productId) {
        return productRepository.delete(productId);
    }
    
    /**
     * 在庫状況を確認
     */
    public int getStockLevel(int productId) {
        ProductService.Product product = productRepository.findById(productId);
        return product != null ? product.getStock() : 0;
    }
    
    /**
     * 在庫を更新
     */
    public boolean updateStock(int productId, int newStock) {
        ProductService.Product product = productRepository.findById(productId);
        if (product != null) {
            product.setStock(newStock);
            return productRepository.update(product);
        }
        return false;
    }
}
