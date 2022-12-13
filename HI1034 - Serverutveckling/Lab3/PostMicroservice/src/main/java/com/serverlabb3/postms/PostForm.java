package com.serverlabb3.postms;

import org.springframework.web.multipart.MultipartFile;

public record PostForm(String content, String token, MultipartFile image, String reportsString) {
}