package com.example.ec.dto;

public class LoginResult {
    private final boolean success;
    private final String role;
    private final String displayName;
    private final Integer id;

    public LoginResult(boolean success, String role, String displayName, Integer id) {
        this.success = success;
        this.role = role;
        this.displayName = displayName;
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getId() {
        return id;
    }
}
