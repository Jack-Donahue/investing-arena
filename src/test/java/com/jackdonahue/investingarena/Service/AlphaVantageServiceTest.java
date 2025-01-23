package com.jackdonahue.investingarena.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

public class AlphaVantageServiceTest {
    //Injects the ObjectMapper mock directly into the service that's being tested
    @InjectMocks
    private AlphaVantageService alphaVantageService;

    //Creates a mock instance that essentially simulates the behavior of the ObjectMapper/RestTemplate
    //Without actually accessing the API
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;

    //Initializes the mock and injectMock
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStockTest() throws Exception {
        // Mocked API response as a JSON string
        String response = """
        {
            "Name": "Apple Inc",
            "MarketCapitalization": "2500000000000",
            "PERatio": "28",
            "PreviousClose": "145.7",
            "Volume": "75000000"
        }
        """;

        //Ensures the mock restTemplate returns our response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(response);
        //Same as above but also parses the JSON String into a JSON Node
        when(objectMapper.readTree(response)).thenReturn(new ObjectMapper().readTree(response));

        Map<String, Object> result = alphaVantageService.getStockData("AAPL");

        //Tests
        assertNotNull(result);
        assertEquals("Apple Inc", result.get("Name"));
        assertEquals("2500000000000", result.get("Market Cap"));
        assertEquals(new BigDecimal("28"), result.get("PE Ratio"));
        assertEquals(new BigDecimal("145.7"), result.get("Previous Close"));
        assertEquals("75000000", result.get("Daily Volume"));
    }
}
