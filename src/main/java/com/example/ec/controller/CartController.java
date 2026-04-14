package com.example.ec.controller;

import com.example.ec.entity.Product;
import com.example.ec.service.CartService;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    public CartController(CartService cartService, ProductService productService) { this.cartService = cartService; this.productService = productService; }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId, @RequestParam(defaultValue = "1") Integer quantity, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
        Product product = productService.getProductById(productId);
        try { cartService.addToCart(customerId, product, quantity); redirectAttributes.addFlashAttribute("successMessage", product.getProductName() + " をカートに追加しました。"); }
        catch (IllegalArgumentException ex) { redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/products";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.removeFromCart((Integer) session.getAttribute("LOGIN_USER_ID"), productId);
        redirectAttributes.addFlashAttribute("successMessage", "商品をカートから削除しました。");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam Integer productId, @RequestParam Integer quantity, HttpSession session, RedirectAttributes redirectAttributes) {
        try { cartService.updateQuantity((Integer) session.getAttribute("LOGIN_USER_ID"), productId, quantity); redirectAttributes.addFlashAttribute("successMessage", "カートを更新しました。"); }
        catch (IllegalArgumentException ex) { redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
        model.addAttribute("cart", cartService.getCart(customerId));
        model.addAttribute("cartTotal", cartService.getCartTotal(customerId));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "cart";
    }
}
