package com.ntou.auctionSite.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "帳號不可為空")
    private String id;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, message = "密碼至少 6 個字元")
    private String password;

    private String email;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}