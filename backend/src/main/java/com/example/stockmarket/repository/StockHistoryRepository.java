package com.example.stockmarket.repository;

import com.example.stockmarket.domain.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByStockNameOrderByDateAsc(String stockName);
}