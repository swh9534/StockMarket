package com.example.stockmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
public class Stock {
    @Id
    private String stockId;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false)
    private int stockPrice;

    public Stock(String stockId, String stockName, int stockPrice) {
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock ID cannot be null or empty");
        }
        if (stockName == null || stockName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock name cannot be null or empty");
        }
        if (stockPrice <= 0) {
            throw new IllegalArgumentException("Stock price must be positive");
        }
        this.stockId = stockId;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
    }

    // 주식 가격 변동 시뮬레이션
    public void updatePrice() {
        Random random = new Random();
        double changePercent = (random.nextDouble() * 0.2 - 0.1); // -10% ~ +10% 변동
        this.stockPrice = (int) (this.stockPrice * (1 + changePercent));
        if (this.stockPrice < 100) this.stockPrice = 100; // 최소 가격 100원 보장
    }

    @Override
    public String toString() {
        return "Stock [id=" + stockId + ", name=" + stockName + ", price=" + stockPrice + "]";
    }
}