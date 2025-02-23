package com.example.bookblog.services;

import com.example.bookblog.models.Book;
import com.example.bookblog.repositories.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooksByTitleOrAuthor(String title, String author) {
        return bookRepository.findBooksByTitleOrAuthor(title, author);
    }

    public Book getBookById(int id) {
        return bookRepository.findBookById(id);
    }

    public Book getBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }
}
