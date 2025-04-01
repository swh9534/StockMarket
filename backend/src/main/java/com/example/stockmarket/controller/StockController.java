package com.example.stockmarket.controller;

import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.domain.StockHistory;
import com.example.stockmarket.dto.AddStockRequest;
import com.example.stockmarket.service.StockService;
import com.example.stockmarket.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;
    private final UserService userService;

    public StockController(StockService stockService, UserService userService) {
        this.stockService = stockService;
        this.userService = userService;
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStock(
            @RequestBody AddStockRequest addStockRequest,
            HttpSession session) {
        // 세션에서 사용자 정보 확인
        String userId = (String) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        // 권한 검증
        if (userId == null || isAdmin == null || !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", "Admin privileges required"));
        }

        try {
            Stock newStock = stockService.addStock(
                    addStockRequest.getStockName(),
                    addStockRequest.getInitialPrice()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid stock data", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Stock addition failed", e.getMessage()));
        }
    }

    @GetMapping("/{stockName}/history")
    public ResponseEntity<List<StockHistory>> getStockHistory(@PathVariable String stockName) {
        List<StockHistory> history = stockService.getStockHistory(stockName);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}

// 에러 응답 클래스
class ErrorResponse {
    private final String error;
    private final String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}