package com.example.stockmarket.service;

import com.example.stockmarket.domain.User;
import com.example.stockmarket.exception.AuthenticationException;

public interface UserService {
    User login(String userId, String password) throws AuthenticationException;
    User register(String userId, String password, String playerName, int initialCash);
    boolean isAdmin(String userId, String password) throws AuthenticationException;
    User findById(String userId);
}