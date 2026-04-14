package com.example.ec.controller;

import com.example.ec.entity.Product;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping("/products")
    public String productList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) Integer minPrice,
                              @RequestParam(required = false) Integer maxPrice,
                              HttpSession session,
                              Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        model.addAttribute("categories", productService.getAllCategoryNames());
        List<Product> products;
        if (minPrice != null || maxPrice != null) {
            products = productService.searchByPriceRange(minPrice, maxPrice);
            model.addAttribute("minPrice", minPrice); model.addAttribute("maxPrice", maxPrice);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchByKeyword(keyword); model.addAttribute("searchKeyword", keyword);
        } else if (category != null && !category.trim().isEmpty()) {
            products = productService.getProductsByCategory(category); model.addAttribute("selectedCategory", category);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "product-list";
    }

    @GetMapping("/products/{productId}")
    public String productDetail(@PathVariable Integer productId, HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Product product = productService.getProductById(productId);
        if (product == null || !product.isActive()) return "redirect:/products";
        model.addAttribute("product", product);
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "product-detail";
    }
}
