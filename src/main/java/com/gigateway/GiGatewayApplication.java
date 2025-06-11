package com.gigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiGatewayApplication.class, args);
	}

}
