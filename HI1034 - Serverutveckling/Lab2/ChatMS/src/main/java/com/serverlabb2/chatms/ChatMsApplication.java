package com.serverlabb2.chatms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class ChatMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMsApplication.class, args);
    }

}
