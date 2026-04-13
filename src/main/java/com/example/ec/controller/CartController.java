package com.example.ec.controller;

import com.example.ec.entity.Product;
import com.example.ec.service.CartService;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(productId);
        if (product != null && product.getStockQuantity() > 0) {
            cartService.addToCart(product, quantity, session);
            redirectAttributes.addFlashAttribute("successMessage", product.getProductName() + " をカートに追加しました。");
        }

        return "redirect:/products";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Integer productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(productId, session);
        redirectAttributes.addFlashAttribute("successMessage", "商品をカートから削除しました。");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        cartService.updateQuantity(productId, quantity, session);
        redirectAttributes.addFlashAttribute("successMessage", "カートを更新しました。");
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, org.springframework.ui.Model model) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "cart";
    }
}
