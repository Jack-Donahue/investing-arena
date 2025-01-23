package com.jackdonahue.investingarena.Controller;

import com.jackdonahue.investingarena.Service.AlphaVantageService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    //The service reference to fetch the stock data
    private final AlphaVantageService avService = new AlphaVantageService();

    //Wrap the output from AlphaVantageService n a ResponseEntity
    @GetMapping("/{symbol}")
    public ResponseEntity<Map<String, Object>> getStock(@PathVariable String ticker) {
        //Call the service and store the output in a map
        Map<String, Object> vantageData = avService.getStockData(ticker);

        //Return Error 404 if not found
        if(vantageData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(vantageData);
    }
}
