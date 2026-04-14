package com.example.ec.controller;

import com.example.ec.entity.Customer;
import com.example.ec.entity.Order;
import com.example.ec.service.AuthService;
import com.example.ec.service.CartService;
import com.example.ec.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {
    private final CartService cartService;
    private final OrderService orderService;
    private final AuthService authService;
    public CheckoutController(CartService cartService, OrderService orderService, AuthService authService) { this.cartService = cartService; this.orderService = orderService; this.authService = authService; }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
        if (cartService.getCart(customerId).isEmpty()) return "redirect:/cart";
        Customer customer = authService.getCustomer(customerId);
        String defaultAddress = customer == null ? "" : ((customer.getAddressLine1() == null ? "" : customer.getAddressLine1()) + (customer.getAddressLine2() == null || customer.getAddressLine2().isBlank() ? "" : " " + customer.getAddressLine2())).trim();
        model.addAttribute("cart", cartService.getCart(customerId));
        model.addAttribute("cartTotal", cartService.getCartTotal(customerId));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        model.addAttribute("defaultAddress", defaultAddress);
        return "checkout";
    }

    @PostMapping("/order/confirm")
    public String confirmOrder(@RequestParam String shippingAddress, @RequestParam String paymentMethod, HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
        try {
            Order order = orderService.createOrder(customerId, cartService.getCart(customerId), shippingAddress, paymentMethod);
            cartService.clearCart(customerId);
            return "redirect:/order/complete/" + order.getOrderId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("cart", cartService.getCart(customerId));
            model.addAttribute("cartTotal", cartService.getCartTotal(customerId));
            model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
            model.addAttribute("defaultAddress", shippingAddress);
            return "checkout";
        }
    }

    @GetMapping("/order/complete/{orderId}")
    public String orderComplete(@PathVariable Integer orderId, HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Order order = orderService.getOrderById(orderId);
        if (order == null) return "redirect:/home";
        model.addAttribute("order", order); model.addAttribute("orderItems", orderService.getOrderItems(orderId)); model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "order-complete";
    }
}
