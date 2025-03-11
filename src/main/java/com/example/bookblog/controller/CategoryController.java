package com.example.bookblog.controller;

import com.example.bookblog.dto.CategoryWithUsersDto;
import com.example.bookblog.entity.Category;
import com.example.bookblog.exception.CategoryAlreadyExistException;
import com.example.bookblog.exception.CategoryNotFoundException;
import com.example.bookblog.service.CategoryService;
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
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService groupService;

    @Autowired
    public CategoryController(CategoryService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<String> registrationGroup(@RequestBody Category group) {
        try {
            groupService.registration(group);
            return ResponseEntity.ok().body("Group add");
        } catch (CategoryAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error T_T");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryWithUsersDto> getOneGroupPath(@PathVariable Long id) {
        try {
            CategoryWithUsersDto group = groupService.getOne(id);
            return ResponseEntity.ok(group);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.ok("Group deleted successfully");
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(@PathVariable Long id,
                                              @RequestBody Category updatedGroup) {
        try {
            groupService.updateGroup(id, updatedGroup);
            return ResponseEntity.ok("Group updated successfully!");
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error...");
        }
    }
}

