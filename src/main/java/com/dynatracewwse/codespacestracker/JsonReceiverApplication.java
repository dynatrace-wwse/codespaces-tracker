package com.dynatracewwse.codespacestracker;

import java.util.Base64;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JsonReceiverApplication {

    private static String tokenAsString = "ilovedynatrace";
    public static final String ENCODEDTOKEN = Base64.getEncoder().encodeToString(tokenAsString.getBytes());


    public static void main(String[] args) {
        SpringApplication.run(JsonReceiverApplication.class, args);
    }
}
