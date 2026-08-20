package com.appzoi.appzoi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.appzoi.appzoi")

public class AppzoiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppzoiApplication.class, args);



    }
}