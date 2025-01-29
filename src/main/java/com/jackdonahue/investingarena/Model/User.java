package com.jackdonahue.investingarena.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private double balance;

    //Key is the stock ticker and the Integer is the amount owned
    private Map<String, Integer> portfolio;

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

    public Map<String, Integer> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Map<String, Integer> portfolio) {
        this.portfolio = portfolio;
    }

    //Adds the amount of the stock to the user's portfolio. If the user doesn't have this stock,
    //It will be initialized with the given amount. Also check to make sure user has sufficient funds
    public boolean buy(String ticker, int amt, double price) {
        double cost = price * amt;

        if(cost > this.balance) {
            return false;
        }
        this.balance -= cost;
        this.portfolio.put(ticker, this.portfolio.getOrDefault(ticker, 0) + amt);
        return true;
    }

    //Remove the amount of the stock from the user's portfolio. Checks things like: is the stock in the user's
    //portfolio, is the amount correct, and also updates the user's balance
    public boolean sell(String ticker, int amt, double price) {
        //Checks for invalid requests
        if (!this.portfolio.containsKey(ticker) || this.portfolio.get(ticker) < amt) {
            return false;
        }

        double total = price * amt;
        this.balance += total;

        //Updates the portfolio, possibly removing the ticker if the user sold all shares
        int remainder = this.portfolio.get(ticker) - amt;
        if (remainder == 0) {
            this.portfolio.remove(ticker);
        } else {
            this.portfolio.put(ticker, remainder);
        }
        return true;
    }
}
