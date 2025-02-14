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

    //Return a summary of the user's portfolio like: list of stocks, total value, and the total return in $/%
    public Map<String, Object> getPortfolioSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("stocks", stocks);  // Returns the map of stocks
        summary.put("totalValue", getTotalValue());
        summary.put("totalReturnDollars", getTotalReturnDollars(BigDecimal.ZERO));  // Replace with actual balance
        summary.put("totalReturnPercent", getTotalReturnPercent(BigDecimal.ZERO));  // Replace with actual balance
        return summary;
    }

    //Returns the amount of money the user has gained/lost
    public BigDecimal getTotalReturnDollars(BigDecimal userBalance) {
        BigDecimal totalValue = getTotalValue();
        BigDecimal totalAssets = totalValue.add(userBalance);
        return totalAssets.subtract(BigDecimal.valueOf(10000));
    }

    //Returns the percent gain/loss of the user's combined assets
    public BigDecimal getTotalReturnPercent(BigDecimal userBalance){
        BigDecimal totalValue = getTotalValue();
        BigDecimal totalAssets = totalValue.add(userBalance);
        return totalAssets.divide(BigDecimal.valueOf(10000)).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100));
    }

    // Getters and setters
    public Map<String, Stock> getStocks() {
        return stocks;
    }
    public void setStocks(Map<String, Stock> stocks) {
        this.stocks = stocks;
    }
}
