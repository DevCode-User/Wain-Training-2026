package com.example.ec.controller;

import com.example.ec.entity.Product;
import com.example.ec.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiProductController {
    private final ProductService productService;
    public ApiProductController(ProductService productService) { this.productService = productService; }
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> listProducts(@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String sort, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "20") Integer limit) {
        int normalizedPage = page == null || page < 1 ? 1 : page;
        int normalizedLimit = limit == null ? 20 : Math.min(Math.max(limit, 1), 100);
        Page<Product> products = productService.searchForApi(keyword, categoryId, sort, normalizedPage, normalizedLimit);
        List<Map<String, Object>> list = products.getContent().stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>(); m.put("productId", p.getProductId()); m.put("productName", p.getProductName()); m.put("price", p.getPrice()); m.put("stockQty", p.getStock()); m.put("imageUrl", p.getImageUrl()); return m; }).toList();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", "success"); body.put("message", "商品一覧を取得しました"); body.put("totalCount", products.getTotalElements()); body.put("page", normalizedPage); body.put("products", list); body.put("errorCode", null);
        return ResponseEntity.ok(body);
    }
}
