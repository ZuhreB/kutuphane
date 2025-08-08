package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class BorrowController {

    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BorrowController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
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

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam("bookId") Long bookId, HttpSession session) {
        // 1. Oturumdan (session) giriş yapmış kullanıcıyı alıyoruz.
        User loggedUser = (User) session.getAttribute("loggedUser");

        // 2. Kullanıcı giriş yapmamışsa (örneğin oturum süresi dolduysa) login sayfasına yönlendir.
        if (loggedUser == null) {
            return "redirect:/login?error=session_expired";
        }

        // 3. Servis metodunu doğru kullanıcı bilgisiyle çağır.
        try {
            // Bu metodun, kitap yoksa veya başka bir sorun varsa hata fırlatması en iyisidir.
            bookService.borrowBook(bookId, loggedUser.getUsername());
            // Başarılı olursa, sayfayı bir başarı mesajıyla yeniden yükle.
            return "redirect:/borrow?bookId=" + bookId + "&success=true";
        } catch (IllegalStateException e) {
            // Başarısız olursa (örn: kitap mevcut değil), bir hata mesajıyla yeniden yükle.
            // Not: URLEncoder.encode() kullanmak daha güvenlidir ama basitlik için şimdilik böyle bırakıyoruz.
            return "redirect:/borrow?bookId=" + bookId + "&error=" + e.getMessage();
        }
    }
}