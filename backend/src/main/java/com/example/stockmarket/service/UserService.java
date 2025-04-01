package com.example.stockmarket.service;

import com.example.stockmarket.domain.User;
import com.example.stockmarket.exception.AuthenticationException;

import java.util.List;

public interface UserService {
    User login(String userId, String password) throws AuthenticationException;
    User register(String userId, String password, String playerName, int initialCash);
    boolean isAdmin(String userId);
    User findById(String userId);
    List<User> getAllUsers();
}