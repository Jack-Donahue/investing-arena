package com.jackdonahue.investingarena.Service;

import com.jackdonahue.investingarena.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //Saves a user object in Redis
    public boolean saveUser(User user) {
        String key = "user:" + user.getUsername();
        //Checking for duplicates
        if(redisTemplate.hasKey(key)) {
            System.out.println("Username already exists, please try another one");
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
        //Get the user and the total cost of the stock the user wants to buy
        User user = getUser(username);
        double cost = price * amt;

        //Check for sufficient funds
        if(cost > user.getBalance()) {
            System.out.println("Insufficient funds to purchase " + amt + " shares of " + ticker);
            return false;
        }
        //Set the new balance of the user, add the stock to the user's portfolio, and update the user's data to redis
        user.setBalance(user.getBalance() - cost);
        user.getPortfolio().put(ticker, user.getPortfolio().getOrDefault(ticker, 0) + amt);
        redisTemplate.opsForValue().set("user:" + username, user);

        return true;
    }

    //Remove the amount of the stock from the user's portfolio. Checks things like: is the stock in the user's
    //portfolio, is the amount correct, and also updates the user's balance
    public boolean sell(String username, String ticker, int amt, double price) {
        User user = getUser(username);
        //Checks for invalid requests
        if (!user.getPortfolio().containsKey(ticker) || user.getPortfolio().get(ticker) < amt) {
            System.out.println("You have either entered the wrong ticker symbol or you don't have this stock in your portfolio");
            return false;
        }
        //Set the new balance
        double total = price * amt;
        user.setBalance(user.getBalance() + total);

        //Updates the portfolio, possibly removing the ticker if the user sold all shares
        int remainder = user.getPortfolio().get(ticker) - amt;
        if (remainder == 0) {
            user.getPortfolio().remove(ticker);
        } else {
            user.getPortfolio().put(ticker, remainder);
        }
        //Updates the user data to redis
        redisTemplate.opsForValue().set("user:" + username, user);
        return true;
    }
}
