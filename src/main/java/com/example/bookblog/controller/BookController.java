package com.example.bookblog.controller;

import com.example.bookblog.entity.Book;
import com.example.bookblog.exception.BookAlreadyExistException;
import com.example.bookblog.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/books")
@Tag(name = "Book Controller", description =
        "API для управления книгами")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Получение всех книг", description = "Возвращает список всех книг")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Получение книги по ID", description = "Возвращает книгу по ее ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Книга найдена"),
        @ApiResponse(responseCode = "404", description = "Книга с таким ID не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Добавление новой книги", description = "Создает новую книгу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Книга успешно добавлена"),
        @ApiResponse(responseCode = "400", description = "Ошибка при добавлении книги")
    })
    @PostMapping
    public ResponseEntity<String> createBook(@RequestBody Book book) {
        try {
            bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body("Book created successfully");
        } catch (BookAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding book: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Обновление книги", description = "Обновляет информацию о книге по ее ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Книга успешно обновлена"),
        @ApiResponse(responseCode = "400", description = "Ошибка при обновлении книги")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        try {
            bookService.updateBook(id, updatedBook);
            return ResponseEntity.ok("Book updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating book");
        }
    }

    @Operation(summary = "Удаление книги", description = "Удаляет книгу по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Книга успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Книга с таким ID не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok("Book deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

