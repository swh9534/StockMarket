package com.example.stockmarket.service;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;

import java.util.List;

public interface PlayerService {
    Player findById(String playerId);
    List<PlayerStock> getPlayerPortfolio(String playerId);
    void buyStock(String playerId, String stockName, int quantity);
    void sellStock(String playerId, String stockName, int quantity);
}