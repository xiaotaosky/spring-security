package com.secure.token;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class RestAuthenticationMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestAuthenticationMasterApplication.class, args);
    }
}
