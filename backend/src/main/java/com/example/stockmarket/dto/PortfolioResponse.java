package com.example.stockmarket.dto;

import com.example.stockmarket.domain.PlayerStock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PortfolioResponse {
    private List<PlayerStock> portfolio;
    private long totalAssets;

    public PortfolioResponse(List<PlayerStock> portfolio, long totalAssets) {
        this.portfolio = portfolio;
        this.totalAssets = totalAssets;
    }
}