package com.serverlabb2.utilities.forms;

import org.springframework.web.multipart.MultipartFile;

public record ImageForm(MultipartFile image) {
}
