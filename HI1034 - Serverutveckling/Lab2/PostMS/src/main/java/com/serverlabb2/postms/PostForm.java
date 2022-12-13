package com.serverlabb2.postms;

import org.springframework.web.multipart.MultipartFile;

public record PostForm(String content, String token, MultipartFile image, String reportsString) {
}