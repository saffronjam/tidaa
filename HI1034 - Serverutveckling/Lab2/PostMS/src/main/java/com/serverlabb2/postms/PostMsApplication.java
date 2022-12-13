package com.serverlabb2.postms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class PostMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostMsApplication.class, args);
    }

}
