package com.jackdonahue.investingarena.Controller;

import com.jackdonahue.investingarena.Service.AlphaVantageService;
import com.jackdonahue.investingarena.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AlphaVantageService alphaVantageService;

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
