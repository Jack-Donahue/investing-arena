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
        try {
            //Handle Http response
            String response = restTemplate.getForObject(url, String.class);
            //Parse response into readable Java object
            JsonNode root = objectMapper.readTree(response);


            //Get relevant data to display to the user
            String name = root.path("Name").asText();
            String marketCap = root.path("MarketCapitalization").asText();
            BigDecimal peRatio = new BigDecimal(root.path("PERatio").asText("0"));
            BigDecimal price = new BigDecimal(root.path("PreviousClose").asText("0"));
            String volume = root.path("Volume").asText();

            // Store values in a map to return
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("Name", name);
            stockData.put("Market Cap", marketCap);
            stockData.put("PE Ratio", peRatio);
            stockData.put("Price", price);
            stockData.put("Daily Volume", volume);

            //Print the data for testing
            System.out.println("Stock Name: " + name);
            System.out.println("Market Cap: " + marketCap);
            System.out.println("PE Ratio: " + peRatio);
            System.out.println("Current Price: $" + price);
            System.out.println("Daily Volume: " + volume);

            return stockData;
        } catch (Exception e) {
            System.err.println("Error fetching stock data: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
