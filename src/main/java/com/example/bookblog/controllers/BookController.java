package com.example.bookblog.controllers;

import com.example.bookblog.models.Book;
import com.example.bookblog.services.BookService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getBooksByTitleOrAuthor(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) String author) {
        return bookService.getBooksByTitleOrAuthor(title, author);
    }

    @GetMapping("/id/{id}")
    public Book getBookById(@PathVariable int id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/title/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        return bookService.getBookByTitle(title);
    }
}
