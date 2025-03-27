package com.example.stockmarket.service;

import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findByStockName(String stockName) {
        return stockRepository.findByStockName(stockName).orElse(null);
    }

    public Stock addStock(String stockName, int initialPrice) {
        if (stockName == null || stockName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock name cannot be null or empty");
        }
        if (initialPrice <= 0) {
            throw new IllegalArgumentException("Initial price must be positive");
        }
        if (stockRepository.findByStockName(stockName).isPresent()) {
            throw new IllegalArgumentException("Stock with name " + stockName + " already exists");
        }
        String stockId = "STOCK" + String.format("%03d", stockRepository.findAll().size() + 1);
        Stock newStock = new Stock(stockId, stockName, initialPrice);
        return stockRepository.save(newStock);
    }

    @Scheduled(fixedRate = 30000, initialDelay = 30000) // 30초마다 실행
    public void simulateMarketFluctuation() {
        List<Stock> stocks = stockRepository.findAll();
        StringBuilder log = new StringBuilder();
        for (Stock stock : stocks) {
            int oldPrice = stock.getStockPrice();
            stock.updatePrice();
            int newPrice = stock.getStockPrice();
            log.append(String.format("%s: %d ⭢ %d  ", stock.getStockName(), oldPrice, newPrice));
            stockRepository.save(stock);
        }
        System.out.println("\n" + log.toString().trim());
        System.out.println("💹 주식 시장이 변동되었습니다.");
    }
}