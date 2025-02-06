package com.jackdonahue.investingarena.Model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private Map<String, Stock> stocks;

    public Portfolio(Map<String, Stock> stocks) {
        this.stocks = stocks;
    }

    //Calculate total value by iterating through every stock's value
    public BigDecimal getTotalValue() {
        BigDecimal total = BigDecimal.ZERO;

        for(Stock stock : stocks.values()) {
            total = total.add(stock.getValue());
        }
        return total;
    }

    // Getters and setters
    public Map<String, Stock> getStocks() {
        return stocks;
    }
    public void setStocks(Map<String, Stock> stocks) {
        this.stocks = stocks;
    }
}
