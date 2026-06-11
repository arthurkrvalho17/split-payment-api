package com.psp.split_payment_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SplitPaymentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitPaymentApiApplication.class, args);
	}

}
