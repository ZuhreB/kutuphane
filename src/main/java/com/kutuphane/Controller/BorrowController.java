package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class BorrowController {

    private final BookService bookService;

    public BorrowController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/borrow")
    public String showBorrowPage(@RequestParam("bookId") Long bookId, Model model) {
        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            model.addAttribute("currentDate", LocalDateTime.now());
            return "borrow-page";
        }
        return "redirect:/books?notFound=true";
    }

    @PostMapping("/api/borrow")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> borrowBookAjax(
            @RequestParam("bookId") Long bookId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Oturum sona erdi. Lütfen tekrar giriş yapın.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            bookService.borrowBook(bookId, loggedUser.getUsername());
            response.put("success", true);
            response.put("message", "Kitap başarıyla ödünç alındı!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/borrow-page")
    public String bookDetails(@RequestParam Long bookId, Model model) {
        List<Book> book = bookService.findById(bookId);
        model.addAttribute("book", book);
        return "borrow-page";
    }

}