package com.jackdonahue.investingarena.Service;

import com.jackdonahue.investingarena.Model.Portfolio;
import com.jackdonahue.investingarena.Model.Stock;
import com.jackdonahue.investingarena.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Used for strictly unit testing, when there are no spring containers involved
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    //ValueOperations is used indirectly in redisTemplate.opsForValue
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserService userService;


    //Testing the service to see if it can successfully retrieve the user
    @Test
    void testGetUser_Success() {
        String username = "donny";
        User mockUser = new User(username);
        String key = "user:" + username;

        // Mock opsForValue() to return the valueOperations mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        //Make sure the key exists, then return the mocked user from valueOperations
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(valueOperations.get(key)).thenReturn(mockUser);

        //Test the service
        User user = userService.getUser(username);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    //Testing the service to see if it will return null for a nonexistent user
    @Test
    void testGetUser_NotFound() {
        String username = "Notfound";
        String key = "user:" + username;

        //Return null from valueOperations.get because the user should be simulated as nonexistent
        when(redisTemplate.hasKey(key)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUser(username));
        assertEquals("User not found", exception.getMessage());
    }

    //Testing the service to check if it can successfully save a new user
    @Test
    void testSaveUser_Success() {
        User user = new User("donny");
        String key = "user:" + user.getUsername();

        // Mock opsForValue() to return the valueOperations mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        //Mock hasKey should return false because the user shouldn't exist yet
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean result = userService.saveUser(user);

        //Test that the user has been saved and that the data has been saved in Redis
        assertTrue(result);
        verify(valueOperations).set(key, user);
    }

    //Testing the saveUser method to see if it will return null if the user already exists
    @Test
    void testSaveUser_Exists() {
        User user = new User("donny");
        String key = "user:" + user.getUsername();

        //Mock hasKey should return true because the user should already exist
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = userService.saveUser(user);

        //Test that the user has not been saved in redis. never.set() essentially checks if any info was every added to redis
        assertFalse(result);
        verify(valueOperations, never()).set(anyString(), any());
    }

    //Testing a successful buy method with sufficient funds
    @Test
    void testBuy_Success() {
        //Create a user and stock, as well as the amount we'd like to buy and a balance that can afford it
        String username = "donny";
        String ticker = "TSLA";
        int amt = 10;
        double price = 150;
        User user = new User(username);

        //Mock redis
        when(redisTemplate.hasKey("user:" + username)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:" + username)).thenReturn(user);

        boolean result = userService.buy(username, ticker, amt, price);

        //Test to make sure that the buy is success, the funds match up, the stock is added, and more
        assertTrue(result);
        assertEquals(10000 - (amt * price), user.getBalance());

        Stock stock = user.getPortfolio().getStocks().get(ticker);
        assertNotNull(stock);
        assertEquals(amt, stock.getShares());
        assertEquals(BigDecimal.valueOf(price), stock.getAveragePrice());

        //Make sure that the user has been saved to redis
        verify(valueOperations).set("user:" + username, user);
    }

    //Test an attempted buy but the user has insufficient funds
    @Test
    void testBuy_InsufficientFunds() {
        //Create a user and stock with way too many shares
        String username = "donny";
        String ticker = "TSLA";
        int amt = 10000;
        double price = 150;
        User user = new User(username);

        String key = "user:" + username;

        //Mock redis
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get("user:" + username)).thenReturn(user);

        boolean result = userService.buy(username, ticker, amt, price);

        //Test that the buy method returns false, and verify the user data wasn't uploaded to redis
        assertFalse(result);
        verify(valueOperations, never()).set(anyString(), any(User.class));
    }

    @Test
    void testBuy_ExistingStock() {
        //Create a user and stocks
        String username = "donny";
        String ticker = "TSLA";
        int amt = 10;
        int newAmt = 5;
        double price = 150;
        User user = new User(username);

        String key = "user:" + username;

        //Create a portfolio and add TSLA stock in there so that it already exists when buying
        Stock stock = new Stock(ticker, amt, BigDecimal.valueOf(price));
        user.getPortfolio().getStocks().put(ticker, stock);

        //Mock redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(valueOperations.get(key)).thenReturn(user);


        boolean result = userService.buy(username, ticker, newAmt, price);

        //Test a successful buy
        assertTrue(result);
        assertEquals(stock, user.getPortfolio().getStocks().get(ticker));

        //Check that the shares have been updated with the proper amount
        assertEquals(amt + newAmt, stock.getShares());

        verify(valueOperations).set(eq("user:" + username), any(User.class));
    }

    @Test
    void testSell_AllShares() {
        //Create a user and a stock
        String username = "donny";
        String ticker = "TSLA";
        int amt = 10;
        double price = 150;
        User user = new User(username);
        String key = "user:" + username;

        //Add the stock to the user's portfolio
        Stock stock = new Stock(ticker, amt, BigDecimal.valueOf(price));
        user.getPortfolio().getStocks().put(ticker, stock);

        // Mock Redis
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(key)).thenReturn(user);

        boolean result = userService.sell(username, ticker, amt,  price);

        //Make sure the stock no longer exists in the user's portfolio
        assertTrue(result);
        assertFalse(user.getPortfolio().getStocks().containsKey(ticker));

        //Check that Redis was updated
        verify(valueOperations).set(eq(key), any(User.class));
    }

    @Test
    void testSell_TooManyShares() {
        //Create a user and a stock
        String username = "donny";
        String ticker = "TSLA";
        int amt = 10;
        double price = 150;
        User user = new User(username);
        String key = "user:" + username;

        //Add the stock to the user's portfolio
        Stock stock = new Stock(ticker, amt, BigDecimal.valueOf(price));
        user.getPortfolio().getStocks().put(ticker, stock);

        // Mock Redis
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(key)).thenReturn(user);

        boolean result = userService.sell(username, ticker, amt + 1, price);

        //Portfolio did not change and there was a failure to sell
        assertFalse(result);
        assertEquals(amt, user.getPortfolio().getStocks().get(ticker).getShares());

        //Make sure Redis was not changed
        verify(valueOperations, never()).set(anyString(), any(User.class));
    }

    @Test
    void testSell_UnownedStock() {
        // Create a user
        String username = "donny";
        String ticker = "TSLA";
        int sellAmt = 5;
        double price = 150;
        User user = new User(username);
        String key = "user:" + username;

        // Mock Redis
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(key)).thenReturn(user);

        // Try to sell TSLA even though we never added it to the user's portfolio
        boolean result = userService.sell(username, ticker, sellAmt, price);

        //Make sure Redis wasn't updated and the sale was unsuccessful
        assertFalse(result);
        verify(valueOperations, never()).set(anyString(), any(User.class));
    }
}
