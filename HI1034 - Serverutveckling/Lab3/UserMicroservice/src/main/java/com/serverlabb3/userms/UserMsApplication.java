package com.serverlabb3.userms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@ComponentScan(basePackages = {"com.serverlabb3.utilities", "com.serverlabb3.userms"})
public class UserMsApplication {
    @Component
    public class MyListener implements ApplicationListener<ServletWebServerInitializedEvent> {

        @Override
        public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
            int port = event.getWebServer().getPort();
            System.out.println("Listening on port: " + port);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(UserMsApplication.class, args);
    }
}
