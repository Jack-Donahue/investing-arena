package com.jackdonahue.investingarena.Model;

import java.math.BigDecimal;
import com.jackdonahue.investingarena.Service.AlphaVantageService;

//A stock class that I can always add onto if I believe we should store other data in here
public class Stock {
    private final String ticker;
    private int shares;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;

    public Stock(String ticker, int shares, BigDecimal averagePrice) {
        this.ticker = ticker;
        this.shares = shares;
        this.averagePrice = averagePrice;
        this.currentPrice = averagePrice;
    }

    //Get the stock's current price through AVService, might delete
    public BigDecimal updateCurrentPrice(AlphaVantageService alphaVantageService) {
        return (BigDecimal) alphaVantageService.getStockData(ticker).get("Price");
    }

    //Get the return in dollars the user has gained/lost
    public BigDecimal getReturnDollars() {
        BigDecimal returnPerShare = currentPrice.subtract(averagePrice);
        return returnPerShare.multiply(BigDecimal.valueOf(shares));
    }

    //Get the return in percent the user has gained/lost
    public BigDecimal getReturnPercent() {
        return currentPrice.divide(averagePrice)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100));
    }

    //Get the percent of the user's portfolio that this stock makes up
    public BigDecimal getPercentOfPortfolio(BigDecimal totalPortfolioValue) {
        return getValue().divide(totalPortfolioValue).multiply(BigDecimal.valueOf(100));
    }

    //Get the current value of the user's shares
    public BigDecimal getValue() {
        return currentPrice.multiply(BigDecimal.valueOf(shares));
    }

    // Getters and Setters
    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
}
