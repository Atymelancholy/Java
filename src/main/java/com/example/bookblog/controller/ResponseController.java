package com.example.bookblog.controller;

import com.example.bookblog.dto.ResponseDto;
import com.example.bookblog.entity.Response;
import com.example.bookblog.exception.BookNotFoundException;
import com.example.bookblog.exception.PostNotFoundException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.service.ResponseService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/responses")
public class ResponseController {
    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/user/{userId}/book/{bookId}")
    public ResponseEntity<String> createResponse(@PathVariable Long userId,
                                                 @PathVariable Long bookId,
                                                 @RequestBody ResponseDto responseDto) {
        try {
            responseService.createResponse(userId, bookId, responseDto.getContent());
            return ResponseEntity.ok("Review successfully added");
        } catch (UserNotFoundException | BookNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error...");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Response>> getUserResponses(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(responseService.getUserResponses(userId));
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Response>> getBookResponses(@PathVariable Long bookId) {
        try {
            return ResponseEntity.ok(responseService.getBookResponses(bookId));
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{responseId}")
    public ResponseEntity<String> deleteResponse(@PathVariable Long responseId) {
        try {
            responseService.deleteResponse(responseId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{responseId}")
    public ResponseEntity<String> updateResponse(@PathVariable Long responseId,
                                                 @RequestBody ResponseDto responseDto) {
        try {
            responseService.updateResponse(responseId, responseDto.getContent());
            return ResponseEntity.ok("Review updated successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error...");
        }
    }
}
