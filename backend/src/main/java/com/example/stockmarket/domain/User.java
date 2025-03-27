package com.example.stockmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String password; // 비밀번호

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player; // 연결된 플레이어 객체

    public User(String userId, String password, Player player) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.userId = userId;
        this.password = password;
        this.player = player;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", playerId=" + (player != null ? player.getPlayerId() : "null") + "]";
    }
}