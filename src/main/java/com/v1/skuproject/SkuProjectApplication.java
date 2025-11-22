package com.v1.skuproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SkuProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkuProjectApplication.class, args);
    }

}
