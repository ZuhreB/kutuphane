package com.kutuphane.Controller;

import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // <--- Bu import'u ekleyin
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employee/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAuthorized(session)) {
            response.put("success", false);
            response.put("message", "Bu işlemi yapmak için yetkiniz yok.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            bookService.deleteBook(id);
            response.put("success", true);
            response.put("message", "Kitap başarıyla silindi (görünümden kaldırıldı)."); // <--- Mesajı güncelleyin
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage()); // BookInUseException'dan gelen mesajı kullanın
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        } catch (Exception e) {
            // Beklenmeyen diğer tüm hatalar
            response.put("success", false);
            response.put("message", "Kitap silinirken beklenmeyen bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
        }
    }


    private boolean isAuthorized(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && ("EMPLOYEE".equals(loggedUser.getRole()) || "ADMIN".equals(loggedUser.getRole()));
    }
}