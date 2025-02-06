package com.jackdonahue.investingarena.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private double balance;

    //Key is the stock ticker and the Integer is the amount owned
    private Map<String, Stock> portfolio;

    //Constructor
    public User(String username) {
        this.username = username;
        this.balance = 10000;
        this.portfolio = new HashMap<>();
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

    public Map<String, Stock> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Map<String, Stock> portfolio) {
        this.portfolio = portfolio;
    }
}
