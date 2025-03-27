package com.example.stockmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    private String playerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int cash;

    public Player(String playerId, String name, int cash) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (cash < 0) {
            throw new IllegalArgumentException("Cash cannot be negative");
        }
        this.playerId = playerId;
        this.name = name;
        this.cash = cash;
    }

    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", name=" + name + ", cash=" + cash + "]";
    }
}