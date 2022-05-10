package com.serverlabb3.imagems;

import com.serverlabb3.utilities.bodies.ExceptionBody;
import com.serverlabb3.utilities.bodies.IdBody;
import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.forms.ImageForm;
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
            var id = imageService.add(form);
            return ResponseEntity.ok(new IdBody(id));
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }


}
