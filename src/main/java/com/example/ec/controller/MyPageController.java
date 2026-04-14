package com.example.ec.controller;

import com.example.ec.entity.Order;
import com.example.ec.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MyPageController {
    private final OrderService orderService;
    public MyPageController(OrderService orderService) { this.orderService = orderService; }
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Integer customerId = (Integer) session.getAttribute("LOGIN_USER_ID");
        model.addAttribute("orders", orderService.getCustomerOrders(customerId));
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "mypage";
    }
    @GetMapping("/mypage/order/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, HttpSession session, Model model) {
        if (!"CUSTOMER".equals(session.getAttribute("LOGIN_ROLE"))) return "redirect:/login";
        Order order = orderService.getOrderById(orderId);
        if (order == null) return "redirect:/mypage";
        model.addAttribute("order", order); model.addAttribute("orderItems", orderService.getOrderItems(orderId)); model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "order-detail";
    }
}
