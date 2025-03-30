package com.example.stockmarket.controller;

import com.example.stockmarket.domain.User;
import com.example.stockmarket.dto.LoginRequest;
import com.example.stockmarket.dto.RegisterRequest;
import com.example.stockmarket.dto.UserResponse;
import com.example.stockmarket.exception.AuthenticationException;
import com.example.stockmarket.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            User user = userService.login(loginRequest.getUserId(), loginRequest.getPassword());

            // 세션에 사용자 정보 저장
            session.setAttribute("userId", user.getUserId());

            // 민감한 정보를 제외한 응답 객체 생성
            UserResponse response = new UserResponse(
                    user.getUserId(),
                    user.getPlayer().getName(),
                    user.getPlayer().getCash(),
                    user.getPlayer().getPlayerId());

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
        try {
            // 세션에서 사용자 정보 제거
            session.removeAttribute("userId");
            session.invalidate();

            return ResponseEntity.ok(new SuccessResponse("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Logout error", e.getMessage()));
        }
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

            // 민감한 정보를 제외한 응답 객체 생성
            UserResponse response = new UserResponse(
                    user.getUserId(),
                    user.getPlayer().getName(),
                    user.getPlayer().getCash(),
                    user.getPlayer().getPlayerId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration error", e.getMessage()));
        }
    }

    @PostMapping("/admin/check")
    public ResponseEntity<?> isAdmin(@RequestBody LoginRequest loginRequest) {
        try {
            boolean isAdmin = userService.isAdmin(loginRequest.getUserId(), loginRequest.getPassword());
            return ResponseEntity.ok(new AdminResponse(isAdmin));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Admin check error", e.getMessage()));
        }
    }

    // 현재 로그인된 사용자 정보 확인 (세션 체크)
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not logged in", "No active session found"));
        }

        try {
            User user = userService.findById(userId);
            UserResponse response = new UserResponse(user.getUserId(), user.getPlayer().getName(),
                    user.getPlayer().getCash(), user.getPlayer().getPlayerId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving user data", e.getMessage()));
        }
    }

    // 응답 클래스들
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

    private static class AdminResponse {
        private final boolean isAdmin;

        public AdminResponse(boolean isAdmin) {
            this.isAdmin = isAdmin;
        }

        public boolean isAdmin() { return isAdmin; }
    }
}