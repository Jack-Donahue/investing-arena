package com.jackdonahue.investingarena.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


//This class is responsible for sending and processing data to the Alpha Vantage API

@Service
public class AlphaVantageService {
    //Alpha Vantage API Key
    private final String apiKey = "ZaWF975JqVnDew5WGCpwk70NNEhvGicY";
    //Base URL for Alpha Vantage to append queries onto
    private final String baseUrl = "https://www.alphavantage.co/query";
    //Inject RestTemplate into class, responsible for making HTTP requests
    private final RestTemplate restTemplate;
    //Used to convert JSON responses into Java objects
    private final ObjectMapper objectMapper = new ObjectMapper();

    //The constructor injects the RestTemplate
    public AlphaVantageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //Function to grab relevant data given the ticker symbol
    //Using Map because it is easy to add/subtract more variables if we choose to
    public Map<String, Object> getStockData(String ticker) {
        //Create the URL using the ticker and the API key
        String url = baseUrl + "?function=OVERVIEW&symbol=" + ticker + "&apikey=" + apiKey;
        Map<String, Object> stockData = new HashMap<>();
        try {
            //Handle Http response
            String response = restTemplate.getForObject(url, String.class);
            //Parse response into readable Java object
            JsonNode root = objectMapper.readTree(response);

            // Store relevant data in a map to return
            stockData.put("Name", root.path("Name").asText());
            stockData.put("Price", getPrice(ticker));
            stockData.put("PE Ratio", new BigDecimal(root.path("PERatio").asText("0")));
            stockData.put("Market Cap", root.path("MarketCapitalization").asText());
            stockData.put("Dividend Per Share", new BigDecimal(root.path("DividendPerShare").asText("0")));
            stockData.put("50 Day Moving Average", new BigDecimal(root.path("50DayMovingAverage").asText("0")));
        } catch (Exception e) {
            System.err.println("Error fetching stock data: " + e.getMessage());
        }
        return stockData;
    }

    // Fetch the real-time stock price
    private BigDecimal getPrice(String ticker) {
        //Create url for 1 min interval of the ticker
        String url = baseUrl + "?function=TIME_SERIES_INTRADAY&symbol=" + ticker
                + "&interval=1min&apikey=" + apiKey;
        try {
            //Get the response from AlphaVantage
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode timeSeries = root.path("Time Series (1min)");

            if (timeSeries.isObject() && timeSeries.size() > 0) {
                // Get the most recent entry
                String latestTime = timeSeries.fieldNames().next();
                double currentPrice = timeSeries.get(latestTime).path("4. close").asDouble();
                return BigDecimal.valueOf(currentPrice);
            }
        } catch (Exception e) {
            System.err.println("Error fetching stock price: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}