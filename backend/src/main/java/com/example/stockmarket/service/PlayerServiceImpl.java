package com.example.stockmarket.service;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;
import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.dto.PortfolioResponse; // 새로 추가
import com.example.stockmarket.repository.PlayerRepository;
import com.example.stockmarket.repository.PlayerStockRepository;
import com.example.stockmarket.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final PlayerStockRepository playerStockRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, StockRepository stockRepository, PlayerStockRepository playerStockRepository) {
        this.playerRepository = playerRepository;
        this.stockRepository = stockRepository;
        this.playerStockRepository = playerStockRepository;
    }

    @Override
    public Player findById(String playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    @Override
    public PortfolioResponse getPlayerPortfolio(String playerId) { // 반환 타입 변경
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        List<PlayerStock> portfolio = playerStockRepository.findByPlayerPlayerId(playerId);

        // 총 주식 가치 계산
        long totalStockValue = portfolio.stream()
                .mapToLong(PlayerStock::getTotalValue)
                .sum();

        // 총 자산 = 현금 + 주식 가치 합계
        long totalAssets = player.getCash() + totalStockValue;

        return new PortfolioResponse(portfolio, totalAssets);
    }

    @Override
    public void buyStock(String playerId, String stockName, int quantity) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        Stock stock = stockRepository.findByStockName(stockName)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockName));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        int totalCost = stock.getStockPrice() * quantity;
        if (player.getCash() < totalCost) {
            throw new IllegalStateException("Not enough cash. Required: " + totalCost + ", Available: " + player.getCash());
        }

        player.setCash(player.getCash() - totalCost);

        List<PlayerStock> playerStocks = playerStockRepository.findByPlayerPlayerId(playerId);
        PlayerStock existingStock = playerStocks.stream()
                .filter(ps -> ps.getStock().getStockId().equals(stock.getStockId()))
                .findFirst()
                .orElse(null);

        if (existingStock != null) {
            existingStock.addQuantity(quantity, totalCost);
            playerStockRepository.save(existingStock);
        } else {
            PlayerStock newPlayerStock = new PlayerStock(stock, quantity, totalCost, player);
            playerStockRepository.save(newPlayerStock);
        }

        playerRepository.save(player);
    }

    @Override
    public void sellStock(String playerId, String stockName, int quantity) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        Stock stock = stockRepository.findByStockName(stockName)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockName));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        List<PlayerStock> playerStocks = playerStockRepository.findByPlayerPlayerId(playerId);
        PlayerStock playerStock = playerStocks.stream()
                .filter(ps -> ps.getStock().getStockId().equals(stock.getStockId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Player does not own this stock: " + stockName));

        if (playerStock.getQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock to sell. Owned: " + playerStock.getQuantity() + ", Requested: " + quantity);
        }

        playerStock.removeQuantity(quantity);
        int totalRevenue = stock.getStockPrice() * quantity;

        player.setCash(player.getCash() + totalRevenue);

        if (playerStock.getQuantity() == 0) {
            playerStockRepository.delete(playerStock);
        } else {
            playerStockRepository.save(playerStock);
        }

        playerRepository.save(player);
    }
}