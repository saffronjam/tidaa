package com.serverlabb2.chatms;


import com.serverlabb2.utilities.bodies.ExceptionBody;
import com.serverlabb2.utilities.exceptions.BackendException;
import com.serverlabb2.utilities.forms.ChatForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

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
    public ResponseEntity<Object> getPreviews(@PathVariable String token) {
        try {
            var chat = chatService.getPreviewsByUserToken(token);
            return ResponseEntity.ok(chat);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @GetMapping("/chats/{token}/{chatId}")
    public ResponseEntity<Object> getByToken(@PathVariable String token, @PathVariable Long chatId) {
        try {
            var chat = chatService.get(token, chatId);
            return ResponseEntity.ok(chat);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }
}
