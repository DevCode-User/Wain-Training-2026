package com.example.ec.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginForm {

    @NotBlank(message = "ログインIDを入力してください。")
    private String loginId;

    @NotBlank(message = "パスワードを入力してください。")
    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
