package com.example.stockmarket.controller;

import com.example.stockmarket.domain.User;
import com.example.stockmarket.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String userId, @RequestParam String password) {
        try {
            User user = userService.login(userId, password);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestParam String userId, @RequestParam String password,
                                         @RequestParam String playerName, @RequestParam int initialCash) {
        try {
            User user = userService.register(userId, password, playerName, initialCash);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<Boolean> isAdmin(@RequestParam String userId, @RequestParam String password) {
        boolean isAdmin = userService.isAdmin(userId, password);
        return ResponseEntity.ok(isAdmin);
    }
}