package com.company.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortenerApplication.class, args);
    }
}
