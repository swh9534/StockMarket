package com.example.stockmarket.repository;

import com.example.stockmarket.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
}