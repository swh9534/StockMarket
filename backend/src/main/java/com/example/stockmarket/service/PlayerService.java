package com.example.stockmarket.service;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.dto.PortfolioResponse; // 새로 추가

import java.util.List;

public interface PlayerService {
    Player findById(String playerId);
    PortfolioResponse getPlayerPortfolio(String playerId); // 수정
    void buyStock(String playerId, String stockName, int quantity);
    void sellStock(String playerId, String stockName, int quantity);
}