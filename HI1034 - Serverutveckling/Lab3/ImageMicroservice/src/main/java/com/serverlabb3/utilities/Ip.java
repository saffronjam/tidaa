package com.serverlabb3.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app")
public class Ip {
    private String chatMs;
    private String imageMs;
    private String postMs;
    private String userMs;
    private String vertxMs;
    private String canvasMs;

    public String getChatMs() {
        return chatMs;
    }

    public String getImageMs() {
        return imageMs;
    }

    public String getPostMs() {
        return postMs;
    }

    public String getUserMs() {
        return userMs;
    }

    public String getVertxMs() {
        return vertxMs;
    }

    public String getCanvasMs() {
        return canvasMs;
    }

    public void setChatMs(String chatMs) {
        this.chatMs = chatMs;
    }

    public void setImageMs(String imageMs) {
        this.imageMs = imageMs;
    }

    public void setPostMs(String postMs) {
        this.postMs = postMs;
    }

    public void setUserMs(String userMs) {
        this.userMs = userMs;
    }

    public void setVertxMs(String vertxMs) {
        this.vertxMs = vertxMs;
    }

    public void setCanvasMs(String canvasMs) {
        this.canvasMs = canvasMs;
    }
}
