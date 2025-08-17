package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BorrowRepository;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.BorrowService;
import com.kutuphane.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
@Controller
public class BorrowController {

    private final BorrowService borrowService;
    private final BookService bookService;
    private final UserService userService;

    public BorrowController(BorrowService borrowService, BookService bookService, UserService userService, BorrowRepository borrowRepository){
        this.borrowService=borrowService;
        this.bookService = bookService;
        this.userService = userService;
    }
    @GetMapping("/employee/fragments/borrowed")
    public String getBorrowedBooksFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }

        model.addAttribute("borrows", borrowService.getBorrowedBooks());
        return "fragments/list-borrowed :: contentFragment";
    }
    @GetMapping("/employee/fragments/borrows/new")
    public String getLendBookFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        // Ödünç verme için gerekli veriler (örneğin kitaplar, kullanıcılar)
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("users", userService.getAllUsers());
        System.out.println("aesrdyugoıjpkjhu.........................");
        return "fragments/lend-book-form :: contentFragment";
    }

    @PostMapping("/api/borrows/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createBorrow(
            @RequestBody Map<String, Long> requestData,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        System.out.println(".................heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeey......................");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            response.put("success", false);
            response.put("message", "Yetkisiz işlem");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Long userId = requestData.get("userID");
            Long bookId = requestData.get("bookID");

            // Ödünç verme işlemini gerçekleştir
            Borrow borrow = borrowService.lendBook(userId, bookId);

            System.out.println("nerdeyseeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            response.put("success", true);
            response.put("message", "Kitap ödünç verildi");
            response.put("borrowId", borrow.getBorrowID());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/api/borrows/{id}/return")
    public ResponseEntity<?> handleBookReturn(@PathVariable("id") Long borrowId, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return ResponseEntity.status(403).body(Map.of("message", "Bu işlem için yetkiniz yok."));
        }
        try {
            borrowService.returnBook(borrowId);
            return ResponseEntity.ok(Map.of("message", "Kitap başarıyla iade alındı."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
