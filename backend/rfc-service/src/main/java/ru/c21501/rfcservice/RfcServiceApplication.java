package ru.c21501.rfcservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RfcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RfcServiceApplication.class, args);
    }

}
