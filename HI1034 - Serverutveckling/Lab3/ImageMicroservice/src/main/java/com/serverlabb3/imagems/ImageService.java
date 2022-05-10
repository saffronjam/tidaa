package com.serverlabb3.imagems;

import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.forms.ImageForm;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public InputStreamResource getAsResource(Long id) {
        try {
            return new InputStreamResource(new ByteArrayResource(get(id).getImageData()).getInputStream());
        } catch (IOException exception) {
            throw new BackendException("Failed to load image data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Image get(Long imageId) {
        try {
            if (imageId < 0) {
                throw new BackendException("Invalid imageId", HttpStatus.BAD_REQUEST);
            }
            var imageOpt = imageRepository.findById(imageId);
            if (imageOpt.isEmpty()) {
                throw new BackendException("Image not found", HttpStatus.NOT_FOUND);
            }
            return imageOpt.get();
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch image by imageId", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Long add(ImageForm form) {
        try {
            byte[] byteArray;
            try {
                byteArray = form.image().getBytes();
            } catch (IOException e) {
                throw new BackendException("Failed to load image data", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            var image = new Image(form.image().getOriginalFilename(), byteArray);
            imageRepository.save(image);
            return image.getId();
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to create image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
