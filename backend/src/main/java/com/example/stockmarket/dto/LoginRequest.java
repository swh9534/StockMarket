package com.example.stockmarket.dto;

// 로그인 요청 DTO
public class LoginRequest {
    private String userId;
    private String password;

    // 기본 생성자
    public LoginRequest() {}

    // 파라미터 생성자
    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    // Getter 및 Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
