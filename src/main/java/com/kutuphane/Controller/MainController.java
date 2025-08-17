package com.kutuphane.Controller;
import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BookRepository;
import com.kutuphane.Repository.BorrowRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BookRepository bookRepository; // BookRepository'yi inject edin

    @Autowired
    private BorrowRepository borrowRepository; // BorrowRepository'yi inject edin

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        System.out.println("Giriş Yapan Kullanıcının Rolü: " + loggedUser.getRole());

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("pageTitle", "Kütüphane Ana Sayfası");

        List<Book> books = bookRepository.findAll();
        populateExpectedReturnDates(books); // expectedReturnDate'leri dolduran yardımcı metot
        model.addAttribute("books", books); // Doldurulmuş Book listesini gönder

        model.addAttribute("contentFragment", "fragments/main-content :: contentFragment");
        return "layout";
    }

    private void populateExpectedReturnDates(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return;
        }
        books.forEach(book -> {
            if (book.getAvailableCopies() == 0) {
                List<Borrow> activeBorrows = borrowRepository.findByBookBookIDAndActualReturnDateIsNullOrderByReturnDateAsc(book.getBookID());

                if (!activeBorrows.isEmpty()) {

                    System.out.println("neden burdayım");
                    book.setExpectedReturnDate(LocalDate.from(activeBorrows.get(0).getReturnDate()));
                }
            }
        });
    }
}