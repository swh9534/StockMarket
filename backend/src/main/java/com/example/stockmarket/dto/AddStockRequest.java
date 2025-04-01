package com.example.stockmarket.dto;

public class AddStockRequest {
    private String stockName;

    private int initialPrice;

    // Getters and setters
    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }
}