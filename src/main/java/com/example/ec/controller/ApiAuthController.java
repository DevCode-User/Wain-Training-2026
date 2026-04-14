package com.example.ec.controller;

import com.example.ec.dto.ApiLoginRequest;
import com.example.ec.dto.LoginResult;
import com.example.ec.service.AuthService;
import com.example.ec.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    public ApiAuthController(AuthService authService, TokenService tokenService) { this.authService = authService; this.tokenService = tokenService; }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody ApiLoginRequest request) {
        LoginResult result = authService.loginCustomer(request.getUserId(), request.getPassword());
        if (!result.isSuccess()) result = authService.loginAdmin(request.getUserId(), request.getPassword());
        if (!result.isSuccess()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response("failure", "ログインに失敗しました。", null, "E001", null));
        String token = tokenService.issueToken(result.getId(), request.getUserId(), result.getRole());
        return ResponseEntity.ok(response("success", "ログイン成功", token, null, result.getRole()));
    }

    private Map<String, Object> response(String result, String message, String token, String errorCode, String role) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", result); body.put("message", message); body.put("token", token); body.put("errorCode", errorCode); body.put("role", role);
        return body;
    }
}
