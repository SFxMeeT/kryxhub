package com.kryxhub.kryxhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class KryxhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(KryxhubApplication.class, args);
	}

}
