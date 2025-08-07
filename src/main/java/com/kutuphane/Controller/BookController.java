package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam("type") String type) {

        List<Book> results = switch (type.toLowerCase()) {
            case "title" -> bookService.searchByTitle(query);
            case "author" -> bookService.searchByAuthor(query);
            case "isbn" -> bookService.searchByISBN(query);
            case "topic"-> bookService.searchByTopic(query);
            default -> List.of();
        };
        return ResponseEntity.ok(results);
    }
}