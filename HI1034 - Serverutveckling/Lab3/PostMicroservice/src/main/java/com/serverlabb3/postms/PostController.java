package com.serverlabb3.postms;

import com.serverlabb3.utilities.bodies.ExceptionBody;
import com.serverlabb3.utilities.exceptions.BackendException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
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

    @GetMapping("/posts")
    public ResponseEntity<Object> getByList(@RequestParam("userIds") Long[] userIds) {
        try {
            var postVm = postService.getByUserList(userIds);
            return ResponseEntity.ok(postVm);
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
