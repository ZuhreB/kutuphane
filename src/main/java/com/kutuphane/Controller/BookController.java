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

/**
 * Bu Controller, kitap arama işlemleri için JSON verisi sağlayan bir REST API'dır.
 * @RestController, bu sınıftaki tüm metotların doğrudan HTTP yanıt gövdesine
 * veri (genellikle JSON) yazacağını belirtir.
 */
@RestController
@RequestMapping("/api/books") // Bu API'daki tüm yollar /api/books ile başlar.
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
            default -> List.of(); // Boş liste
        };
        return ResponseEntity.ok(results); // Sonuçları 200 OK status kodu ile döndür.
    }
}