package com.example.addressbook;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AddressBookApplication {

    public static void main(String[] args) {
        Dotenv.configure().systemProperties().load();
        SpringApplication.run(AddressBookApplication.class, args);
    }
}