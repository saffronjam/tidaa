package com.serverlabb3.postms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@ComponentScan(basePackages = {"com.serverlabb3.utilities", "com.serverlabb3.postms"})
public class PostMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostMsApplication.class, args);
    }

}
