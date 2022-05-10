package com.backend.bodies;

import com.google.gson.Gson;
import org.springframework.web.multipart.MultipartFile;

public record PostForm(String content, String token, MultipartFile image) {
    public String toJson() {
        return new Gson().toJson(this, PostForm.class);
    }
}
