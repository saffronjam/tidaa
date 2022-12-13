package com.backend.controllers;

import com.backend.BackendException;
import com.backend.bodies.ExceptionBody;
import com.backend.bodies.PostForm;
import com.backend.models.Post;
import com.backend.models.viewmodels.PostVm;
import com.backend.services.PostService;
import com.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;

@RestController
@CrossOrigin
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<Object> get(@PathVariable int userId) {
        try {
            var postVm = postService.getByUser(userId);
            return ResponseEntity.ok(postVm);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @GetMapping("/posts/{token}/friends")
    public ResponseEntity<Object> getFriendsPosts(@PathVariable String token) {
        try {
            var friends = userService.getFriends(token);
            var posts = new ArrayList<PostVm>();
            for (var friend : friends) {
                posts.addAll(postService.getByUser(friend.id()));
            }
            posts.sort((p1, p2) -> p2.created().compareTo(p1.created()));
            return ResponseEntity.ok(posts);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }

    @PostMapping("/posts")
    public ResponseEntity<Object> add(@ModelAttribute PostForm form) {
        try {
            postService.add(form);
            return ResponseEntity.ok(null);
        } catch (BackendException exception) {
            return ResponseEntity.status(exception.getStatusCode()).body(new ExceptionBody(exception.getMessage()));
        }
    }
}
