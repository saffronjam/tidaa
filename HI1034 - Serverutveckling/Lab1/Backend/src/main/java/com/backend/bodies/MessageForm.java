package com.backend.bodies;

import org.springframework.web.multipart.MultipartFile;

public record MessageForm(String content, long chatId, String token, MultipartFile image) {
}
