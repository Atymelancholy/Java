package com.example.bookblog.controller;

import com.example.bookblog.dto.UserWithResponsesAndCategoryDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.exception.UserAlreadyExistException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.service.UserService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registrationUser(@RequestBody User user) {
        try {
            userService.registration(user);
            return ResponseEntity.ok("User add...");
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithResponsesAndCategoryDto> getOneUserPath(@PathVariable Long id) {
        try {
            UserWithResponsesAndCategoryDto user = userService.getOne(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @PostMapping("/{userId}/categories/{groupId}")
    public ResponseEntity<String> addUserToGroup(@PathVariable Long userId,
                                                 @PathVariable Long groupId) {
        try {
            userService.addUserToGroup(userId, groupId);
            return ResponseEntity.ok("User added to group!");
        } catch (UserNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/categories/{groupId}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable Long userId,
                                                      @PathVariable Long groupId) {
        try {
            userService.removeUserFromGroup(userId, groupId);
            return ResponseEntity.ok("User removed from group!");
        } catch (UserNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/categories")
    public ResponseEntity<Set<Category>> getUserGroups(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserGroups(userId));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestBody User updatedUser) {
        try {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User updated successfully!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user.");
        }
    }
}
