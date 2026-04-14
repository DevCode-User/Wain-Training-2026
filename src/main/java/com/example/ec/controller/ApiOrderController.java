package com.example.ec.controller;

import com.example.ec.dto.ApiOrderRequest;
import com.example.ec.entity.Customer;
import com.example.ec.entity.Order;
import com.example.ec.service.AuthService;
import com.example.ec.service.OrderService;
import com.example.ec.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiOrderController {
    private final OrderService orderService;
    private final TokenService tokenService;
    private final AuthService authService;
    public ApiOrderController(OrderService orderService, TokenService tokenService, AuthService authService) { this.orderService = orderService; this.tokenService = tokenService; this.authService = authService; }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestHeader(value = "Authorization", required = false) String authorization, @Valid @RequestBody ApiOrderRequest request) {
        TokenService.TokenUser tokenUser = resolveTokenUser(authorization);
        if (tokenUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("認証に失敗しました。", "E101"));
        if (!"CUSTOMER".equals(tokenUser.getRole())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("利用者トークンでログインしてください。", "E102"));
        Customer customer = authService.getCustomer(tokenUser.getUserPk());
        if (customer == null || !customer.getLoginId().equals(request.getUserId())) return ResponseEntity.badRequest().body(error("userId が認証情報と一致しません。", "E103"));
        try {
            Order order = orderService.createOrderFromApi(tokenUser.getUserPk(), request);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("result", "success"); body.put("message", "注文を受け付けました"); body.put("orderId", order.getOrderId()); body.put("acceptedAt", LocalDateTime.now().toString()); body.put("errorCode", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage(), "E104"));
        }
    }

    private TokenService.TokenUser resolveTokenUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
        return tokenService.resolve(authorization.substring("Bearer ".length()).trim());
    }

    private Map<String, Object> error(String message, String errorCode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", "failure"); body.put("message", message); body.put("orderId", null); body.put("acceptedAt", null); body.put("errorCode", errorCode);
        return body;
    }
}
