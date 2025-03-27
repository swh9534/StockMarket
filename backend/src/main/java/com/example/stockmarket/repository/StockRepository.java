package com.example.stockmarket.repository;

import com.example.stockmarket.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByStockName(String stockName);
}