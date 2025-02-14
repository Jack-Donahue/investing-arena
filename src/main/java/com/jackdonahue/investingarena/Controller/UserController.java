package com.jackdonahue.investingarena.Controller;

import com.jackdonahue.investingarena.Model.Portfolio;
import com.jackdonahue.investingarena.Model.Stock;
import com.jackdonahue.investingarena.Model.User;
import com.jackdonahue.investingarena.Service.AlphaVantageService;
import com.jackdonahue.investingarena.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AlphaVantageService alphaVantageService;

    //Endpoint to creating a user
    @PostMapping("/create")
    public ResponseEntity<String> create(
            @RequestParam String username) {
        User user = new User(username);
        boolean success = userService.saveUser(user);

        if(success) {
            return ResponseEntity.ok("User " + username + " created successfully.");
        } else {
            return ResponseEntity.badRequest().body("Username " + username + " already exists. Please choose a different one.");
        }
    }

    //Endpoint for getting the user details
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userService.getUser(username);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //Endpoint to get all the user's details like portfolio and other stats
    @GetMapping("/{username}/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(@PathVariable String username) {
        Portfolio portfolio = userService.getUser(username).getPortfolio();
        if (portfolio != null) {
            return ResponseEntity.ok(portfolio.getPortfolioSummary());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //Endpoint to buy stocks, calls the UserService.buyStock() and passes data through there
    @PostMapping("/buy")
    public ResponseEntity<String> buy(
            @RequestParam String username,
            @RequestParam String ticker,
            @RequestParam int amt,
            @RequestParam double price) {

        boolean success = userService.buy(username, ticker, amt, price);

        if(success) {
            return ResponseEntity.ok("Your order to purchase " + amt + " shares of $" + ticker +
                    " has been filled at the share price of $" + price + "!.");
        } else {
            return ResponseEntity.badRequest().body("Your order to purchase shares of $" + ticker +
                    " is unsuccessful please try again.");
        }
    }

    //Endpoint to sell stocks, calls the UserService.sellStock() and passes data through there
    @PostMapping("/sell")
    public ResponseEntity<String> sell(
            @RequestParam String username,
            @RequestParam String ticker,
            @RequestParam int amt,
            @RequestParam double price) {

        boolean success = userService.sell(username, ticker, amt, price);

        if (success) {
            return ResponseEntity.ok("Your order to sell " + amt + " shares of $" + ticker +
                    " has been filled at the share price of $" + price + "!.");
        } else {
            return ResponseEntity.badRequest().body("Your order to sell shares of $" + ticker +
                    " is unsuccessful please try again.");
        }
    }
}
