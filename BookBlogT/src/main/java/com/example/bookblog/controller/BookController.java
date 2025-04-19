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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Controller", description =
        "API для управления книгами")
public class BookController {

    @Autowired
    private BookService bookService;

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
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @Operation(summary = "Добавление новой книги", description = "Создает новую книгу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Книга успешно добавлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка при добавлении книги")
    })
    @PostMapping
    public ResponseEntity<Long> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);  // получаем созданную книгу с ID
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook.getId());  // возвращаем только ID книги
        } catch (BookAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // Возвращаем null, если книга уже существует
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // Возвращаем null при внутренней ошибке сервера
        }
    }

    /*@Operation(summary = "Добавление новой книги", description = "Создает новую книгу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Книга успешно добавлена"),
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
    }*/

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

    @Operation(summary = "Добавление списка книг",
            description = "Позволяет добавить несколько книг за один запрос")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Книги успешно добавлены"),
        @ApiResponse(responseCode = "400",
                    description = "Некорректные данные запроса")
    })
    @PostMapping(value = "/bulk")
    public ResponseEntity<List<Book>> addBooksBulk(@RequestBody List<Book> books) {
        List<Book> validBooks = books.stream()
                .peek(book -> {
                    if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                        throw new IllegalArgumentException("Title must not be empty");
                    }
                    if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                        throw new IllegalArgumentException("Author must not be empty");
                    }
                    book.setId(null);
                })
                .filter(book -> !bookService.existsByTitleAndAuthor(book.getTitle(),
                        book.getAuthor()))
                .collect(Collectors.toList());

        if (validBooks.isEmpty()) {
            throw  new IllegalArgumentException("Bad request");
        }

        List<Book> savedBooks = bookService.saveBooksBulk(validBooks);
        return new ResponseEntity<>(savedBooks, HttpStatus.CREATED);
    }

    @Operation(summary = "Получение книг по категории", description = "Возвращает список книг, относящихся к категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Book>> getBooksByCategoryId(@PathVariable Long categoryId) {
        List<Book> books = bookService.getBooksByCategoryId(categoryId);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Добавление категории к книге", description = "Добавляет категорию к книге по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно добавлена к книге"),
            @ApiResponse(responseCode = "404", description = "Книга или категория не найдены")
    })
    @PostMapping("/{bookId}/category/{categoryId}")
    public ResponseEntity<String> addCategoryToBook(@PathVariable Long bookId, @PathVariable Long categoryId) {
        try {
            bookService.addCategoryToBook(bookId, categoryId);
            return ResponseEntity.ok("Category added to book successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book or Category not found");
        }
    }

    @Operation(summary = "Удаление категории из книги", description = "Удаляет категорию из книги по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно удалена из книги"),
            @ApiResponse(responseCode = "404", description = "Книга или категория не найдены")
    })
    @DeleteMapping("/{bookId}/category/{categoryId}")
    public ResponseEntity<String> removeCategoryFromBook(@PathVariable Long bookId, @PathVariable Long categoryId) {
        try {
            bookService.removeCategoryFromBook(bookId, categoryId);
            return ResponseEntity.ok("Category removed from book successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book or Category not found");
        }
    }
}
