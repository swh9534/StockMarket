package com.example.stockmarket.repository;

import com.example.stockmarket.domain.PlayerStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerStockRepository extends JpaRepository<PlayerStock, Long> {
    List<PlayerStock> findByPlayerPlayerId(String playerId);
}