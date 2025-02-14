package com.jackdonahue.investingarena.Service;

import com.jackdonahue.investingarena.Model.Stock;
import com.jackdonahue.investingarena.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //Saves a user object in Redis
    public boolean saveUser(User user) {
        String key = "user:" + user.getUsername();
        //Checking for duplicates
        if(redisTemplate.hasKey(key)) {
            return false;
        }
        redisTemplate.opsForValue().set(key, user);
        return true;
    }

    //Creates the UserKey and returns the user associated with it, if it exists
    public User getUser(String username) {
        String key = "user:" + username;

        if (!redisTemplate.hasKey(key)) {
            throw new RuntimeException("User not found");
        }

        return (User) redisTemplate.opsForValue().get(key);
    }

    //Adds the amount of the stock to the user's portfolio. If the user doesn't have this stock,
    //It will be initialized with the given amount. Also check to make sure user has sufficient funds
    public boolean buy(String username, String ticker, int amt, double price) {
        User user = getUser(username);
        Map<String, Stock> stocks = user.getPortfolio().getStocks();
        Stock stock = stocks.get(ticker);
        double cost = price * amt;

        //Check for sufficient funds
        if(cost > user.getBalance()) {
            System.out.println("Insufficient funds to purchase " + amt + " shares of " + ticker);
            return false;
        }
        //Set the new balance of the user
        user.setBalance(user.getBalance() - cost);
        //If the stock already exists, overwrite the new quantity and averagePrice
        //If not, create a new Stock instance and add it to the portfolio
        //Update the portfolio and save the new data to redis
        if(stock != null) {
            int shares = stock.getShares();
            stock.setShares(shares + amt);
            BigDecimal oldSum = stock.getAveragePrice().multiply(BigDecimal.valueOf(shares));
            BigDecimal newSum = BigDecimal.valueOf(amt).multiply(BigDecimal.valueOf(price));
            BigDecimal newAverage = (oldSum.add(newSum)).divide(BigDecimal.valueOf(stock.getShares()));
            stock.setAveragePrice(newAverage);
            stocks.put(ticker, stock);
        } else {
            Stock newStock = new Stock(ticker, amt, BigDecimal.valueOf(price));
            stocks.put(ticker, newStock);
        }
        redisTemplate.opsForValue().set("user:" + username, user);
        return true;
    }

    //Remove the amount of the stock from the user's portfolio. Checks things like: is the stock in the user's
    //portfolio, is the amount correct, and also updates the user's balance
    public boolean sell(String username, String ticker, int amt, double price) {
        User user = getUser(username);
        Map<String, Stock> stocks = user.getPortfolio().getStocks();
        Stock stock = stocks.get(ticker);

        //Check for invalid symbol
        if(!stocks.containsKey(ticker)) {
            System.out.println("You do not have " + ticker + " in your portfolio.");
            return false;
        }
        //Check for invalid sell amount
        if(amt > stock.getShares()) {
            System.out.println("You have less than " + amt + " shares of " + ticker + " in your portfolio.");
            return false;
        }
        //Set the new balance
        double total = price * amt;
        user.setBalance(user.getBalance() + total);

        //Updates the portfolio, possibly removing the ticker if the user sold all shares
        int remainder = stock.getShares() - amt;
        if (remainder == 0) {
            stocks.remove(ticker);
        } else {
            stock.setShares(remainder);
            stocks.put(ticker, stock);
        }
        //Updates the user data to redis
        redisTemplate.opsForValue().set("user:" + username, user);
        return true;
    }
}
