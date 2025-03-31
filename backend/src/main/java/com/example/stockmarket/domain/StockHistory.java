package com.example.stockmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_history")
@Getter
@Setter
@NoArgsConstructor
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private LocalDateTime date;

    public StockHistory(String stockName, int price, LocalDateTime date) {
        if (stockName == null || stockName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock name cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.stockName = stockName;
        this.price = price;
        this.date = date != null ? date : LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "StockHistory [id=" + id + ", stockName=" + stockName + ", price=" + price + ", date=" + date + "]";
    }
}