package utilities.forms;

import org.springframework.web.multipart.MultipartFile;

public record ImageForm(MultipartFile image) {
}
