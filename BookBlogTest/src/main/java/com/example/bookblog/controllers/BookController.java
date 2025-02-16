package com.example.bookblog.controllers;

import com.example.bookblog.models.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
final class BookController {
    private static final List<Book> BOOKS = List.of(
            new Book(1, "Токийский_зодиак", "Созди_Симада"),
            new Book(2, "Коллекционер", "Джон_Фаулз"),
            new Book(3, "Молчание_ягнят", "Томас_Харрис")
    );

    @GetMapping
    public List<Book> getBooksByTitleOrAuthor(@RequestParam(required = false)
        final String title,
        @RequestParam(required = false) final String author) {

        if (title == null && author == null) {
            return BOOKS;
        }

        return BOOKS.stream()
                .filter(book -> (book.title().equalsIgnoreCase(title))
                || (book.author().equalsIgnoreCase(author)))
                .collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    public Book getBookById(@PathVariable final int id) {
        return BOOKS.stream()
                .filter(book -> book.id() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }

    @GetMapping("/title/{title}")
    public Book getBookByTitle(@PathVariable final String title) {
        return BOOKS.stream()
                .filter(book -> book.title().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }
}
