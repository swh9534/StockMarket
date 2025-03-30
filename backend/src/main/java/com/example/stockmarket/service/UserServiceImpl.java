package com.example.stockmarket.service;

import com.example.stockmarket.domain.Player;
import com.example.stockmarket.domain.User;
import com.example.stockmarket.exception.AuthenticationException;
import com.example.stockmarket.repository.PlayerRepository;
import com.example.stockmarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    public UserServiceImpl(UserRepository userRepository, PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public User login(String userId, String password) throws AuthenticationException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new AuthenticationException("존재하지 않는 ID입니다.");
        }
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    @Override
    @Transactional
    public User register(String userId, String password, String playerName, int initialCash) {
        if (userRepository.findById(userId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        if (initialCash < 10000) {
            throw new IllegalArgumentException("초기 투자금은 최소 10,000원이어야 합니다.");
        }

        String playerId = "PLAYER" + (userRepository.findAll().size() + 1);
        Player player = new Player(playerId, playerName, initialCash);
        playerRepository.save(player);

        User user = new User(userId, password, player);
        userRepository.save(user);

        return user;
    }

    @Override
    public boolean isAdmin(String userId, String password) throws AuthenticationException {
        // 실제 환경에서는 데이터베이스에서 관리자 정보를 확인하는 것이 좋습니다
        if (!userId.equals("admin")) {
            throw new AuthenticationException("관리자 ID가 아닙니다.");
        }
        if (!password.equals("1234")) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다: " + userId));
    }
}