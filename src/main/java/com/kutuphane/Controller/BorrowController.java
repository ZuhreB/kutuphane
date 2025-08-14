package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BorrowRepository;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
@RequestMapping("/employee/borrows") // İlgili tüm endpoint'leri bu URL altında gruplayalım.
public class BorrowController {

    private final BookService bookService;
    private final UserService userService;
    private final BorrowRepository borrowRepository;

    // Constructor Injection: Bağımlılıkları yönetmenin en modern ve güvenilir yolu.
    public BorrowController(BookService bookService, UserService userService, BorrowRepository borrowRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.borrowRepository = borrowRepository;
    }
    @GetMapping("/new")
    public String showLendBookForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Ödünç Kitap Ver");
        // layout.html'in orta kısmına yüklenecek olan fragment'ı belirtiyoruz.
        model.addAttribute("contentFragment", "fragments/lend-book-form.html :: lend-book-form");
        return "layout";
    }

    @PostMapping("/create")
    public String createBorrow(@RequestParam("username") String username,
                               @RequestParam("bookTitle") String bookTitle,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        try {
            Book bookToBorrow = bookService.searchByTitle(bookTitle)
                    .stream()
                    .filter(book -> book.getAvailableCopies() > 0)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("'" + bookTitle + "' başlıklı uygun bir kitap bulunamadı veya mevcut değil."));

            bookService.borrowBook(bookToBorrow.getBookID(), username);

            redirectAttributes.addFlashAttribute("successMessage",
                    "'" + bookToBorrow.getTitle() + "' kitabı, '" + username + "' kullanıcısına başarıyla ödünç verildi.");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "İşlem başarısız: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Beklenmedik bir hata oluştu: " + e.getMessage());
        }

        return "layout";
    }

}