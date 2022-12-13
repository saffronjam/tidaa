package com.serverlabb2.imagems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class ImageMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageMsApplication.class, args);
    }

}
