package com.example.stockmarket.repository;

import com.example.stockmarket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}