package com.serverlabb2.utilities.forms;

import org.springframework.web.multipart.MultipartFile;

public record MessageForm(String content, long chatId, String token, MultipartFile image, String reportsString) {
}
