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
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private InMemoryCache<Long, Book> bookCache;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void createBook(Book book) {
        // Убедитесь, что id не задан для нового объекта
        if (book.getId() != null) {
            throw new IllegalArgumentException("The id should be null for new books");
        }

        bookRepository.save(book);  // id будет сгенерировано автоматически
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
