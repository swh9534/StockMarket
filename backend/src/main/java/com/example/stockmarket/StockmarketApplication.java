package com.example.stockmarket;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;
import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.domain.User;
import com.example.stockmarket.repository.PlayerRepository;
import com.example.stockmarket.repository.PlayerStockRepository;
import com.example.stockmarket.repository.StockRepository;
import com.example.stockmarket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockmarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockmarketApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(StockRepository stockRepository, PlayerRepository playerRepository, UserRepository userRepository, PlayerStockRepository playerStockRepository) {
        return args -> {
            // 초기 주식 데이터
            if (stockRepository.findAll().isEmpty()) {
                stockRepository.save(new Stock("STOCK001", "Apple", 1500));
                stockRepository.save(new Stock("STOCK002", "Google", 2000));
                System.out.println("초기 주식 데이터가 생성되었습니다.");
            }

            // 초기 플레이어 및 사용자 데이터
            if (playerRepository.findAll().isEmpty()) {
                Player player = new Player("PLAYER001", "John Doe", 50000);
                playerRepository.save(player);

                User user = new User("user1", "pass123", player);
                userRepository.save(user);

                // 초기 PlayerStock 데이터
                Stock appleStock = stockRepository.findByStockName("Apple").orElseThrow();
                PlayerStock playerStock = new PlayerStock(appleStock, 10, 15000, player);
                playerStockRepository.save(playerStock);

                System.out.println("초기 사용자 및 플레이어 주식 데이터가 생성되었습니다.");
            }
        };
    }
}