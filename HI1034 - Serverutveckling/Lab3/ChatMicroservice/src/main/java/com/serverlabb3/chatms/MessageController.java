package com.serverlabb3.chatms;

import com.serverlabb3.utilities.Ip;
import com.serverlabb3.utilities.Requests;
import com.serverlabb3.utilities.bodies.ExceptionBody;
import com.serverlabb3.utilities.bodies.IdBody;
import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.forms.MessageForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class MessageController {

    private final MessageService messageService;
    private final Ip ip;

    public MessageController(MessageService messageService, ChatService chatService, Ip ip) {
        this.messageService = messageService;
        this.ip = ip;
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
        var userIdBody = Requests.getJsonObject(Requests.createGet(ip.getUserMs() + "/validate/" + token), IdBody.class);
        if (userIdBody.id() == -1) {
            throw new BackendException("Invalid user token", HttpStatus.BAD_REQUEST);
        }
        return userIdBody.id();
    }

}
