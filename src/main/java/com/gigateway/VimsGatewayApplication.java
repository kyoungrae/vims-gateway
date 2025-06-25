package com.gigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // 서비스 디스커버리 사용할 경우

@SpringBootApplication
@EnableAsync
public class VimsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(VimsGatewayApplication.class, args);
	}

}
