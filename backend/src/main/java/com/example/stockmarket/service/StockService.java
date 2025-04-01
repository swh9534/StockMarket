package com.example.stockmarket.service;

import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.domain.StockHistory;
import com.example.stockmarket.repository.StockRepository;
import com.example.stockmarket.repository.StockHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public StockService(StockRepository stockRepository, StockHistoryRepository stockHistoryRepository) {
        this.stockRepository = stockRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findByStockName(String stockName) {
        return stockRepository.findByStockName(stockName).orElse(null);
    }

    @Transactional
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
        Stock savedStock = stockRepository.save(newStock);

        // 초기 가격을 히스토리에 저장
        StockHistory history = new StockHistory(stockName, initialPrice, LocalDateTime.now());
        stockHistoryRepository.save(history);

        return savedStock;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 30000) // 30초마다 실행
    @Transactional
    public void simulateMarketFluctuation() {
        List<Stock> stocks = stockRepository.findAll();
        StringBuilder log = new StringBuilder();
        for (Stock stock : stocks) {
            int oldPrice = stock.getStockPrice();
            stock.updatePrice();
            int newPrice = stock.getStockPrice();
            log.append(String.format("%s: %d ⭢ %d  ", stock.getStockName(), oldPrice, newPrice));

            // 히스토리에 이전 가격 저장
            StockHistory history = new StockHistory(stock.getStockName(), oldPrice, LocalDateTime.now());
            stockHistoryRepository.save(history);

            stockRepository.save(stock);
        }
        System.out.println("\n" + log.toString().trim());
        System.out.println("💹 주식 시장이 변동되었습니다.");
    }

    public List<StockHistory> getStockHistory(String stockName) {
        return stockHistoryRepository.findByStockNameOrderByDateAsc(stockName);
    }

}