package com.jackdonahue.investingarena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class InvestingArenaApplication {
	public static void main(String[] args) {
		SpringApplication.run(InvestingArenaApplication.class, args);
	}

}
