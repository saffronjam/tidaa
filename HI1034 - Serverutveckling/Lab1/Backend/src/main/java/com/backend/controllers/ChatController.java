package com.backend.controllers;

import com.backend.BackendException;
import com.backend.bodies.ChatForm;
import com.backend.bodies.ExceptionBody;
import com.backend.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chats")
    public ResponseEntity<Object> create(@RequestBody ChatForm chatForm) {
        try {
            chatService.add(chatForm);
            return ResponseEntity.ok("");
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @GetMapping("/chats/{token}")
    public ResponseEntity<Object> getByToken(@PathVariable String token) {
        try {
            var chat = chatService.getPreviewsByUserToken(token);
            return ResponseEntity.ok(chat);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @GetMapping("/chats/{token}/{friendId}")
    public ResponseEntity<Object> getByToken(@PathVariable String token, @PathVariable Long friendId) {
        try {
            var chat = chatService.get(token, friendId);
            return ResponseEntity.ok(chat);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

}
