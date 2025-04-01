package com.example.stockmarket.controller;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.dto.PortfolioResponse; // 새로 추가
import com.example.stockmarket.dto.StockRequest;
import com.example.stockmarket.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayer(@PathVariable String playerId) {
        Player player = playerService.findById(playerId);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(player);
    }

    @GetMapping("/{playerId}/portfolio")
    public ResponseEntity<PortfolioResponse> getPlayerPortfolio(@PathVariable String playerId) { // 반환 타입 변경
        PortfolioResponse portfolioResponse = playerService.getPlayerPortfolio(playerId);
        return ResponseEntity.ok(portfolioResponse);
    }

    @PostMapping("/{playerId}/buy")
    public ResponseEntity<Map<String, Object>> buyStock(@PathVariable String playerId, @RequestBody StockRequest stockRequest) {
        try {
            playerService.buyStock(playerId, stockRequest.getStockName(), stockRequest.getQuantity());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "주식 매수 완료");
            response.put("stockName", stockRequest.getStockName());
            response.put("quantity", stockRequest.getQuantity());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "매수 실패");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{playerId}/sell")
    public ResponseEntity<Map<String, Object>> sellStock(
            @PathVariable String playerId,
            @RequestBody StockRequest stockRequest) {
        try {
            playerService.sellStock(playerId, stockRequest.getStockName(), stockRequest.getQuantity());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "주식 매도 완료");
            response.put("stockName", stockRequest.getStockName());
            response.put("quantity", stockRequest.getQuantity());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "매도 실패");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}