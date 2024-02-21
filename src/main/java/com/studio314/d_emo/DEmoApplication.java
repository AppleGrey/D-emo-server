package com.studio314.d_emo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EnableWebSocket
public class DEmoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DEmoApplication.class, args);
    }

}
