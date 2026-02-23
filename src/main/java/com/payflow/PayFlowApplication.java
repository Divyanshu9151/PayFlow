package com.payflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PayFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayFlowApplication.class, args);
	}

}
