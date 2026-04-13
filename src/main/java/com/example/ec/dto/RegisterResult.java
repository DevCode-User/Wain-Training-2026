package com.example.ec.dto;

public class RegisterResult {
    private final boolean success;
    private final String message;
    private final Integer customerId;

    public RegisterResult(boolean success, String message, Integer customerId) {
        this.success = success;
        this.message = message;
        this.customerId = customerId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCustomerId() {
        return customerId;
    }
}
