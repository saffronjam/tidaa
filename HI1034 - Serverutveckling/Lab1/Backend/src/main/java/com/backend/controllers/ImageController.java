package com.backend.controllers;

import com.backend.BackendException;
import com.backend.bodies.ExceptionBody;
import com.backend.bodies.ImageForm;
import com.backend.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Object> get(@PathVariable Long id) {
        var resource = imageService.getAsResource(id);
        try {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @PostMapping("/images")
    public ResponseEntity<Object> add(@ModelAttribute ImageForm form) {
        try {
            imageService.add(form);
            return ResponseEntity.ok(null);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }


}
