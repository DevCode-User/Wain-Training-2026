package com.example.ec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "ログインIDを入力してください。")
    @Size(max = 50, message = "ログインIDは50文字以内で入力してください。")
    private String loginId;

    @NotBlank(message = "パスワードを入力してください。")
    @Size(min = 8, max = 100, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank(message = "氏名を入力してください。")
    @Size(max = 100, message = "氏名は100文字以内で入力してください。")
    private String customerName;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスの形式で入力してください。")
    private String customerEmail;

    @NotBlank(message = "郵便番号を入力してください。")
    @Size(max = 10, message = "郵便番号は10文字以内で入力してください。")
    private String postalCode;

    @NotBlank(message = "住所1を入力してください。")
    @Size(max = 255, message = "住所1は255文字以内で入力してください。")
    private String addressLine1;

    @Size(max = 255, message = "住所2は255文字以内で入力してください。")
    private String addressLine2;

    @Size(max = 20, message = "電話番号は20文字以内で入力してください。")
    private String phone;

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
