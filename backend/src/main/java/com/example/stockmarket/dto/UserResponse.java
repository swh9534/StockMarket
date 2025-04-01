package com.example.stockmarket.dto;

public class UserResponse {
    private final String userId;
    private final String playerName;
    private final double cash;
    private final String playerId;
    private final boolean isAdmin;

    public UserResponse(String userId, String playerName, double cash, String playerId, boolean isAdmin) {
        this.userId = userId;
        this.playerName = playerName;
        this.cash = cash;
        this.playerId = playerId;
        this.isAdmin = isAdmin;
    }

    public String getUserId() { return userId; }
    public String getPlayerName() { return playerName; }
    public double getCash() { return cash; }
    public String getPlayerId() { return playerId; }
    public boolean isAdmin() { return isAdmin; }
}