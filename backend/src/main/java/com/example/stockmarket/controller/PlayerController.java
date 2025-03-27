package com.example.stockmarket.controller;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;
import com.example.stockmarket.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> buyStock(@PathVariable String playerId, @RequestParam String stockName, @RequestParam int quantity) {
        try {
            playerService.buyStock(playerId, stockName, quantity);
            return ResponseEntity.ok("주식 매수 완료: " + stockName + " " + quantity + "주");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("매수 실패: " + e.getMessage());
        }
    }

    @PostMapping("/{playerId}/sell")
    public ResponseEntity<String> sellStock(@PathVariable String playerId, @RequestParam String stockName, @RequestParam int quantity) {
        try {
            playerService.sellStock(playerId, stockName, quantity);
            return ResponseEntity.ok("주식 매도 완료: " + stockName + " " + quantity + "주");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("매도 실패: " + e.getMessage());
        }
    }
}