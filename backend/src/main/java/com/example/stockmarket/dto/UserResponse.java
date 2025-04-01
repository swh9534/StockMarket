package com.example.stockmarket.dto;

public class UserResponse {
    private final String userId;
    private final String playerName;
    private final double cash;
    private final String playerId;
    private final boolean isAdmin;
    private final double totalAssets;

    public UserResponse(String userId, String playerName, double cash, String playerId, boolean isAdmin, double totalAssets) {
        this.userId = userId;
        this.playerName = playerName;
        this.cash = cash;
        this.playerId = playerId;
        this.isAdmin = isAdmin;
        this.totalAssets = totalAssets;
    }

    public String getUserId() { return userId; }
    public String getPlayerName() { return playerName; }
    public double getCash() { return cash; }
    public String getPlayerId() { return playerId; }
    public boolean isAdmin() { return isAdmin; }
    public double getTotalAssets() { return totalAssets; }
}