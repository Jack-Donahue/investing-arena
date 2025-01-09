package com.jackdonahue.investingarena.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;



//This class is responsible for sending and processing data to the Yahoo Finance API

@Service
public class YahooFinanceService {

    //The base URL to add Yahoo Finance API commands onto
    private static final String YAHOO_FINANCE_BASE_URL = "https://query1.finance.yahoo.com/v7/finance/quote";

    //Template for making Http requests
    private final RestTemplate restTemplate;

    //Constructor grabs an instance of RestTemplate instance
    public YahooFinanceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //Returns the current price of the stock given the ticker symbol
    public double getPrice(String ticker) {
        //Create the url, adding on the ticker symbol
        String url = UriComponentsBuilder.fromUriString(YAHOO_FINANCE_BASE_URL)
                .queryParam("symbols", ticker)
                .toUriString();
        try {
            //Sends get request
            String response = restTemplate.getForObject(url, String.class);
            //Parse the response into JSON format
            JSONObject jsonResponse = new JSONObject(response);
            double stockPrice = jsonResponse.getJSONObject("quoteResponse")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getDouble("regularMarketPrice");
            return stockPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
