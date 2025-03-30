package com.example.stockmarket.dto;

public class RegisterRequest {
    private String userId;
    private String password;
    private String playerName;
    private int initialCash;

    // 기본 생성자
    public RegisterRequest() {}

    // 파라미터 생성자
    public RegisterRequest(String userId, String password, String playerName, int initialCash) {
        this.userId = userId;
        this.password = password;
        this.playerName = playerName;
        this.initialCash = initialCash;
    }

    // Getter 및 Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getInitialCash() { return initialCash; }
    public void setInitialCash(int initialCash) { this.initialCash = initialCash; }
}