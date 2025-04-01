package com.example.stockmarket.controller;

import com.example.stockmarket.domain.User;
import com.example.stockmarket.dto.LoginRequest;
import com.example.stockmarket.dto.RegisterRequest;
import com.example.stockmarket.dto.UserResponse;
import com.example.stockmarket.dto.PortfolioResponse;
import com.example.stockmarket.exception.AuthenticationException;
import com.example.stockmarket.service.PlayerService;
import com.example.stockmarket.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PlayerService playerService;

    public UserController(UserService userService, PlayerService playerService) {
        this.userService = userService;
        this.playerService = playerService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            User user = userService.login(loginRequest.getUserId(), loginRequest.getPassword());
            session.setAttribute("userId", user.getUserId());
            boolean isAdmin = userService.isAdmin(user.getUserId());
            session.setAttribute("isAdmin", isAdmin);

            PortfolioResponse portfolio = playerService.getPlayerPortfolio(user.getPlayer().getPlayerId());

            UserResponse response = new UserResponse(
                    user.getUserId(),
                    user.getPlayer().getName(),
                    user.getPlayer().getCash(),
                    user.getPlayer().getPlayerId(),
                    isAdmin,
                    portfolio.getTotalAssets()
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new SuccessResponse("Logout successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(
                    registerRequest.getUserId(),
                    registerRequest.getPassword(),
                    registerRequest.getPlayerName(),
                    registerRequest.getInitialCash()
            );

            UserResponse response = new UserResponse(
                    user.getUserId(),
                    user.getPlayer().getName(),
                    user.getPlayer().getCash(),
                    user.getPlayer().getPlayerId(),
                    userService.isAdmin(user.getUserId()),
                    0.0
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration error", e.getMessage()));
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not logged in", "No active session found"));
        }

        try {
            User user = userService.findById(userId);
            boolean isAdmin = userService.isAdmin(userId);
            PortfolioResponse portfolio = playerService.getPlayerPortfolio(user.getPlayer().getPlayerId());

            UserResponse response = new UserResponse(
                    user.getUserId(),
                    user.getPlayer().getName(),
                    user.getPlayer().getCash(),
                    user.getPlayer().getPlayerId(),
                    isAdmin,
                    portfolio.getTotalAssets()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving user data", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (userId == null || isAdmin == null || !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", "Admin privileges required"));
        }

        try {
            List<User> users = userService.getAllUsers();
            List<UserResponse> responses = users.stream()
                    .map(user -> {
                        PortfolioResponse portfolio = playerService.getPlayerPortfolio(user.getPlayer().getPlayerId());
                        return new UserResponse(
                                user.getUserId(),
                                user.getPlayer().getName(),
                                user.getPlayer().getCash(),
                                user.getPlayer().getPlayerId(),
                                userService.isAdmin(user.getUserId()),
                                portfolio.getTotalAssets()
                        );
                    })
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("User fetch failed", e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private final String error;
        private final String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }

    private static class SuccessResponse {
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
