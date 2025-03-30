package com.example.stockmarket.dto;

// 사용자 응답 DTO (비밀번호와 같은 민감한 정보 제외)
public class UserResponse {
    private String userId;
    private String playerName;
    private int cash;
    private String playerId;

    public UserResponse(String userId, String playerName, int cash, String playerId) {
        this.userId = userId;
        this.playerName = playerName;
        this.cash = cash;
        this.playerId = playerId;
    }

    // Getter
    public String getUserId() { return userId; }
    public String getPlayerName() { return playerName; }
    public int getCash() { return cash; }
    public String getPlayerId() { return playerId; }
}
