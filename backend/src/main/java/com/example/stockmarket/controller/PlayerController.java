package com.example.stockmarket.controller;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;
import com.example.stockmarket.dto.StockRequest;
import com.example.stockmarket.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<PlayerStock> getPlayerPortfolio(@PathVariable String playerId) {
        return playerService.getPlayerPortfolio(playerId);
    }

    @PostMapping("/{playerId}/buy")
    public ResponseEntity<Map<String, Object>> buyStock(@PathVariable String playerId, @RequestBody StockRequest stockRequest) {
        try {
            playerService.buyStock(playerId, stockRequest.getStockName(), stockRequest.getQuantity());

            // Map을 사용하여 JSON 응답 반환
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
            @RequestBody StockRequest stockRequest) {  // @RequestParam에서 @RequestBody로 변경
        try {
            playerService.sellStock(playerId, stockRequest.getStockName(), stockRequest.getQuantity());

            // Map을 사용하여 JSON 응답 반환
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