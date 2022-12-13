package com.backend.controllers;

import com.backend.BackendException;
import com.backend.BadLoginException;
import com.backend.bodies.*;
import com.backend.models.viewmodels.ConvertHelpers;
import com.backend.models.viewmodels.UserVm;
import com.backend.services.ChatService;
import com.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public UserController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> get(@PathVariable int id) {
        try {
            var user = userService.get(id);
            return ResponseEntity.ok(user);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String searchFor) {
        try {
            List<UserVm> users;
            if (searchFor == null) {
                users = userService.getAll();
            } else {
                users = userService.getAllLike(searchFor);
            }
            return ResponseEntity.ok(users);

        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> create(@RequestBody UserForm userForm) {
        try {
            userService.add(userForm);
            return ResponseEntity.ok(null);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm) {
        try {
            var userVm = userService.get(loginForm.usernameOrEmail(), loginForm.password());
            return ResponseEntity.ok().body(userVm);
        } catch (BadLoginException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @GetMapping("/users/{token}/friends")
    public ResponseEntity<Object> create(@PathVariable String token) {
        try {
            var user = userService.getFull(token);
            return ResponseEntity.ok(user.getFriends().stream().map(ConvertHelpers::toUserVm).collect(Collectors.toList()));
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @PostMapping("/users/friends")
    public ResponseEntity<Object> addFriend(@RequestBody FriendForm friendForm) {
        try {
            userService.addFriend(friendForm);
            chatService.add(friendForm.token(), friendForm.friendId());
            return ResponseEntity.ok(null);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/friends")
    public ResponseEntity<Object> removeFriend(@RequestBody FriendForm friendForm) {
        try {
            userService.removeFriend(friendForm);
            var user = userService.get(friendForm.token());
            chatService.remove(user.id(), friendForm.friendId());
            return ResponseEntity.ok(null);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

}
