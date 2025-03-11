package com.example.bookblog.controller;

import com.example.bookblog.dto.ResponseDto;
import com.example.bookblog.entity.Response;
import com.example.bookblog.exception.PostNotFoundException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.service.ResponseService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/responses")
public class ResponseController {
    private final ResponseService postService;

    public ResponseController(ResponseService postService) {
        this.postService = postService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> createPost(@PathVariable Long userId,
                                        @RequestBody ResponseDto post) {
        try {
            postService.createPost(userId, post.getContent());
            return ResponseEntity.ok("review successfully added");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error...");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Response>> getUserPosts(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(postService.getUserPosts(userId));
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{responseId}")
    public ResponseEntity<String> deletePost(@PathVariable Long responseId) {
        try {
            postService.deletePost(responseId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{responseId}")
    public ResponseEntity<String> updatePost(@PathVariable Long responseId,
                                             @RequestBody ResponseDto post) {
        try {
            postService.updatePost(responseId, post.getContent());
            return ResponseEntity.ok("Review updated successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error...");
        }
    }
}
