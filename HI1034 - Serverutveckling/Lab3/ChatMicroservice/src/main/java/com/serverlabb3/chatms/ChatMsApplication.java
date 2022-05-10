package com.serverlabb3.chatms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@ComponentScan(basePackages = {"com.serverlabb3.utilities", "com.serverlabb3.chatms"})
public class ChatMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMsApplication.class, args);
    }

}
