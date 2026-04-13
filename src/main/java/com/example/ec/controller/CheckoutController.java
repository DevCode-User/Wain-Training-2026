package com.example.ec.controller;

import com.example.ec.dto.CartItem;
import com.example.ec.entity.Order;
import com.example.ec.entity.Product;
import com.example.ec.service.CartService;
import com.example.ec.service.OrderService;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final ProductService productService;

    public CheckoutController(CartService cartService,
                              OrderService orderService,
                              ProductService productService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));

        return "checkout";
    }

    @PostMapping("/order/confirm")
    public String confirmOrder(@RequestParam String shippingAddress,
                               @RequestParam String paymentMethod,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }

        try {
            List<CartItem> cart = cartService.getCart(session);
            if (cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "カートが空です");
                return "redirect:/cart";
            }

            Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
            Order order = orderService.createOrder(customerId, cart, shippingAddress, paymentMethod);

            // カートをクリア
            cartService.clearCart(session);

            return "redirect:/order/complete/" + order.getOrderId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "注文確定に失敗しました: " + e.getMessage());
            model.addAttribute("cart", cartService.getCart(session));
            model.addAttribute("cartTotal", cartService.getCartTotal(session));
            return "checkout";
        }
    }

    @GetMapping("/order/complete/{orderId}")
    public String orderComplete(@PathVariable Integer orderId,
                                HttpSession session,
                                Model model) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/home";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getOrderItems(orderId));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));

        return "order-complete";
    }

    @GetMapping("/products/{productId}")
    public String productDetail(@PathVariable Integer productId,
                                HttpSession session,
                                Model model) {
        // ログイン確認
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(productId);
        if (product == null || !product.getIsActive()) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));

        return "product-detail";
    }
}
