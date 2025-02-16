package com.example.bookblog.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/books")
public class MainController {

    private static final List<Book> books = List.of(
            new Book(1, "Токийский_зодиак", "Созди_Симада"),
            new Book(2, "Коллекционер", "Джон_Фаулз"),
            new Book(3, "Молчание_ягнят", "Томас_Харрис")
    );

    // GET /api/books?title=Мастер_и_Маргарита
    @GetMapping
    public List<Book> getBooksByTitle(@RequestParam(required = false) String title) {
        if (title == null) {
            return books;
        }
        return books.stream()
                .filter(book -> book.title().equalsIgnoreCase(title))
                .toList();
    }

    // GET /api/books/1
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable final int id) {
        return books.stream()
                .filter(book -> book.id() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }
}

record Book(int id, String title, String author) { }

