package com.serverlabb2.chatms;

import com.serverlabb2.utilities.Requests;
import com.serverlabb2.utilities.bodies.ExceptionBody;
import com.serverlabb2.utilities.bodies.IdBody;
import com.serverlabb2.utilities.exceptions.BackendException;
import com.serverlabb2.utilities.forms.MessageForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService, ChatService chatService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> create(@ModelAttribute MessageForm form) {
        try {
            validateToken(form.token());
            messageService.add(form);
            return ResponseEntity.ok(null);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    private Long validateToken(String token) {
        // Validate token
        var userIdBody = Requests.getJsonObject(Requests.createGet("http://user-ms:8084/validate/" + token), IdBody.class);
        if (userIdBody.id() == -1) {
            throw new BackendException("Invalid user token", HttpStatus.BAD_REQUEST);
        }
        return userIdBody.id();
    }

}
