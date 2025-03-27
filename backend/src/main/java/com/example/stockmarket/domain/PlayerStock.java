package com.example.stockmarket.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_stocks")
@Getter
@Setter
@NoArgsConstructor
public class PlayerStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // JPA 엔티티의 기본 키로 새 필드 추가

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock; // 주식 ID 대신 Stock 객체와의 관계 설정

    @Column(nullable = false)
    private int quantity; // 보유 수량

    @Column(nullable = false)
    private int investedAmount; // 투자한 총 금액

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player; // Player와의 관계 설정

    public PlayerStock(Stock stock, int quantity, int investedAmount, Player player) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (investedAmount < 0) {
            throw new IllegalArgumentException("Invested amount cannot be negative");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.stock = stock;
        this.quantity = quantity;
        this.investedAmount = investedAmount;
        this.player = player;
    }

    // 수량 추가 (매수 시 사용)
    public void addQuantity(int amount, int additionalCost) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive");
        }
        if (additionalCost < 0) {
            throw new IllegalArgumentException("Additional cost cannot be negative");
        }
        this.quantity += amount;
        this.investedAmount += additionalCost;
    }

    // 수량 감소 (매도 시 사용)
    public void removeQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to remove must be positive");
        }
        if (this.quantity < amount) {
            throw new IllegalStateException("Not enough stock to remove");
        }
        double ratio = (double) (this.quantity - amount) / this.quantity;
        this.investedAmount = (int) (this.investedAmount * ratio);
        this.quantity -= amount;
    }

    // 현재 보유 주식의 총 가치 계산
    public int getTotalValue() {
        return stock.getStockPrice() * this.quantity;
    }

    // 수익률 계산 메서드
    public double getProfitRate() {
        if (investedAmount == 0) {
            return 0.0; // 투자 금액이 0이면 수익률 0
        }
        int currentValue = getTotalValue();
        return (double) (currentValue - investedAmount) / investedAmount * 100;
    }

    // 주식 이름 가져오기
    public String getStockName() {
        return stock.getStockName();
    }

    @Override
    public String toString() {
        return "PlayerStock [stock=" + stock.getStockName() + ", quantity=" + quantity + ", investedAmount=" + investedAmount + ", totalValue=" + getTotalValue() + "]";
    }
}