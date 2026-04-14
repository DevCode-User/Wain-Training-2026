package com.example.ec.dto;

import jakarta.validation.constraints.NotBlank;

public class ApiLoginRequest {
    @NotBlank(message = "userId は必須です。")
    private String userId;
    @NotBlank(message = "password は必須です。")
    private String password;
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
