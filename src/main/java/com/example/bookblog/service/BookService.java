package com.example.bookblog.service;

import com.example.bookblog.cache.InMemoryCache;
import com.example.bookblog.entity.Book;
import com.example.bookblog.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final InMemoryCache<Long, Book> book;

    // Renamed the constructor parameter to 'cache' to avoid conflict
    public BookService(BookRepository bookRepository, InMemoryCache<Long, Book> cache) {
        this.bookRepository = bookRepository;
        this.bookCache = cache;
    }

    @Autowired
    private InMemoryCache<Long, Book> bookCache;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void createBook(Book book) {
        // Ensure that id is not set for a new object
        if (book.getId() != null) {
            throw new IllegalArgumentException("The id should be null for new books");
        }

        bookRepository.save(book);  // id will be automatically generated
    }

    public void updateBook(Long id, Book updatedBook) throws Exception {
        Book existingBook = bookRepository.findById(id).orElseThrow(()
                -> new Exception("Book not found"));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        bookRepository.save(existingBook);
        bookCache.put(id, existingBook);
    }

    public void deleteBook(Long id) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow(() -> new Exception("Book not found"));
        bookRepository.delete(book);
        bookCache.remove(id);
    }
}
