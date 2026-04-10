package com.example.system.admin.repository;

import com.example.system.admin.service.ProductService;
import java.util.List;

/**
 * 商品リポジトリ
 */
public class ProductRepository {
    
    /**
     * 全商品を取得
     */
    public List<ProductService.Product> findAll() {
        return ProductService.getAllProducts();
    }
    
    /**
     * IDで商品を取得
     */
    public ProductService.Product findById(int productId) {
        return ProductService.getProductById(productId);
    }
    
    /**
     * 商品を追加
     */
    public boolean save(ProductService.Product product) {
        return ProductService.addProduct(product);
    }
    
    /**
     * 商品を更新
     */
    public boolean update(ProductService.Product product) {
        return ProductService.updateProduct(product);
    }
    
    /**
     * 商品を削除
     */
    public boolean delete(int productId) {
        return ProductService.deleteProduct(productId);
    }
}
