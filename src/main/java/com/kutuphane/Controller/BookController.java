package com.kutuphane.Controller;

import com.kutuphane.Entity.Author;
import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Publisher;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.AuthorService;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.PublisherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // Change to @Controller
import org.springframework.ui.Model; // Import Model
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller // Change this from @RestController to @Controller
@RequestMapping("/employee/books") // Adjusted base request mapping
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService; // Inject AuthorService
    private final PublisherService publisherService; // Inject PublisherService

    @Autowired
    public BookController(BookService bookService, AuthorService authorService, PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
    }

    @DeleteMapping("/delete/{id}") // HTTP DELETE metodunu kullanıyoruz
    @ResponseBody // Metodun döndürdüğü Map'i JSON olarak HTTP yanıt gövdesine yazar
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable("id") Long id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        System.out.println("silcemmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole().toUpperCase()) && !"ADMIN".equals(loggedUser.getRole().toUpperCase()))) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Yetkisiz erişim. Bu işlemi gerçekleştirmek için yetkiniz yok.");
            response.put("messageType", "danger");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            bookService.deleteBook(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Kitap başarıyla silindi.");
            response.put("messageType", "success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Kitap silinirken bir hata oluştu: " + e.getMessage());
            response.put("messageType", "danger");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }