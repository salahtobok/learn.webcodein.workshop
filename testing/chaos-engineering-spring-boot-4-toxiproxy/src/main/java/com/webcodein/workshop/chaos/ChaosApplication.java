package com.webcodein.workshop.chaos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ChaosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChaosApplication.class, args);
    }
}
