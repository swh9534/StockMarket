package com.example.stockmarket;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.PlayerStock;
import com.example.stockmarket.domain.Stock;
import com.example.stockmarket.domain.StockHistory;
import com.example.stockmarket.domain.User;
import com.example.stockmarket.repository.PlayerRepository;
import com.example.stockmarket.repository.PlayerStockRepository;
import com.example.stockmarket.repository.StockRepository;
import com.example.stockmarket.repository.StockHistoryRepository;
import com.example.stockmarket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
public class StockmarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockmarketApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            StockRepository stockRepository,
            PlayerRepository playerRepository,
            UserRepository userRepository,
            PlayerStockRepository playerStockRepository,
            StockHistoryRepository stockHistoryRepository) { // StockHistoryRepository 추가
        return args -> {
            // 초기 주식 데이터
            if (stockRepository.findAll().isEmpty()) {
                Stock apple = new Stock("STOCK001", "Apple", 1500);
                Stock google = new Stock("STOCK002", "Google", 2000);
                stockRepository.save(apple);
                stockRepository.save(google);
                System.out.println("초기 주식 데이터가 생성되었습니다.");

                // 초기 주식 히스토리 데이터
                stockHistoryRepository.save(new StockHistory("Apple", 1500, LocalDateTime.now().minusDays(2)));
                stockHistoryRepository.save(new StockHistory("Apple", 1480, LocalDateTime.now().minusDays(1)));
                stockHistoryRepository.save(new StockHistory("Apple", 1500, LocalDateTime.now()));
                stockHistoryRepository.save(new StockHistory("Google", 2000, LocalDateTime.now().minusDays(2)));
                stockHistoryRepository.save(new StockHistory("Google", 1980, LocalDateTime.now().minusDays(1)));
                stockHistoryRepository.save(new StockHistory("Google", 2000, LocalDateTime.now()));
                System.out.println("초기 주식 히스토리 데이터가 생성되었습니다.");
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