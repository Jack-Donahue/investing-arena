package com.jackdonahue.investingarena.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private double balance;
    private Portfolio portfolio;
    private Map<String, Stock> stocks;

    //Constructor
    public User(String username) {
        this.username = username;
        this.balance = 10000;
        this.stocks = new HashMap<String, Stock>();
        this.portfolio = new Portfolio(stocks);
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
