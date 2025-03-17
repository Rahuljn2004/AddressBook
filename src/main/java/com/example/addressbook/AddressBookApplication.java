package com.example.addressbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AddressBookApplication {

    public static void main(String[] args) {
        Dotenv.configure()
                .systemProperties()
                .load();
        SpringApplication.run(AddressBookApplication.class, args);
    }
}