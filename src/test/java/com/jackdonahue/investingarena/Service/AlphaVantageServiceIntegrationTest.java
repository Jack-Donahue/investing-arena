package com.jackdonahue.investingarena.Service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;


public class AlphaVantageServiceIntegrationTest {
    //This is not a mock test, so we're using the real Service class
    private final AlphaVantageService alphaVantageService;

    public AlphaVantageServiceIntegrationTest() {
        // Use a real RestTemplate for live API calls
        this.alphaVantageService = new AlphaVantageService(new RestTemplate());
    }

    @Test
    void testAPI() {
        Map<String, Object> result = alphaVantageService.getStockData("AAPL");

        //Tests to check if we are actually getting info from AlphaVantage
        assertNotNull(result);
        System.out.println("Stock Data: " + result);
    }
}
