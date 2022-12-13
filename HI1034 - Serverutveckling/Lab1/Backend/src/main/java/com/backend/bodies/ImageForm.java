package com.backend.bodies;

import org.springframework.web.multipart.MultipartFile;

public record ImageForm(MultipartFile image) {
}
