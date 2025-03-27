package com.example.stockmarket.controller;

import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.findAll();
    }

    @PostMapping
    public ResponseEntity<Stock> addStock(@RequestParam String stockName, @RequestParam int initialPrice) {
        try {
            Stock newStock = stockService.addStock(stockName, initialPrice);
            return ResponseEntity.ok(newStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}