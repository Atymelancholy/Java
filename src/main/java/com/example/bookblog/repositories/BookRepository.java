package com.example.bookblog.repositories;

import com.example.bookblog.models.Book;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {

    private static final List<Book> BOOKS = List.of(
            new Book(1, "Токийский_зодиак", "Созди_Симада"),
            new Book(2, "Коллекционер", "Джон_Фаулз"),
            new Book(3, "Молчание_ягнят", "Томас_Харрис")
    );

    public List<Book> findBooksByTitleOrAuthor(String title, String author) {
        if (title == null && author == null) {
            return BOOKS;
        }

        return BOOKS.stream()
                .filter(book -> (book.title().equalsIgnoreCase(title))
                        || (book.author().equalsIgnoreCase(author)))
                .collect(Collectors.toList());
    }

    public Book findBookById(int id) {
        return BOOKS.stream()
                .filter(book -> book.id() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }

    public Book findBookByTitle(String title) {
        return BOOKS.stream()
                .filter(book -> book.title().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
    }
}
