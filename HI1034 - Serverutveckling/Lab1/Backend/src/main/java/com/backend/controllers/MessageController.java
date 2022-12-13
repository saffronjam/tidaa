package com.backend.controllers;

import com.backend.BackendException;
import com.backend.bodies.ExceptionBody;
import com.backend.bodies.MessageForm;
import com.backend.services.ChatService;
import com.backend.services.MessageService;
import com.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService, ChatService chatService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> create(@ModelAttribute MessageForm form) {
        try {
            if (!userService.validUserToken(form.token())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionBody("Invalid user token"));
            }
            messageService.add(form);
            return ResponseEntity.ok(null);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }


}
