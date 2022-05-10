package com.serverlabb3.userms;

import com.serverlabb3.utilities.Ip;
import com.serverlabb3.utilities.bodies.ExceptionBody;
import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.exceptions.BadLoginException;
import com.serverlabb3.utilities.forms.FriendForm;
import com.serverlabb3.utilities.forms.LoginForm;
import com.serverlabb3.utilities.forms.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;
    //private final ChatService chatService;

    @Autowired
    public UserController(UserService userService) { //, ChatService chatService) {
        this.userService = userService;
        //this.chatService = chatService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> get(@PathVariable int id) {
        try {
            var user = userService.login(id);
            return ResponseEntity.ok(user);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @Autowired
    private Ip ip;

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
            var userVm = userService.login(loginForm.usernameOrEmail(), loginForm.password());
            return ResponseEntity.ok().body(userVm);
        } catch (BadLoginException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Object> login(@PathVariable String token) {
        try {
            var id = userService.getIdIfValidToken(token);
            return ResponseEntity.ok().body(id);
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
            //chatService.add(friendForm.token(), friendForm.friendId());
            return ResponseEntity.ok(null);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/friends")
    public ResponseEntity<Object> removeFriend(@RequestBody FriendForm friendForm) {
        try {
            userService.removeFriend(friendForm);
            var user = userService.login(friendForm.token());
            //chatService.remove(user.id(), friendForm.friendId());
            return ResponseEntity.ok(null);
        } catch (BackendException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionBody(ex.getMessage()));
        }
    }
}
