package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private UserService  userService;
    @Autowired
    private BorrowService borrowService;


    @GetMapping("/employee/page")
    public String showAdminPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login"; // Yetkisiz erişim ise giriş sayfasına yönlendir
        }

        model.addAttribute("pageTitle", "Çalışan Paneli");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks());
        return "layout";
    }


    @GetMapping("/employee/fragments/panel")
    public String getPanelFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login"; // Veya AJAX isteği için 401 Unauthorized yanıtı döndür
        }


        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks()); // Eğer panelde kitap listesi varsa
        return "fragments/main-content :: contentFragment";
    }

    @GetMapping("/employee/fragments/books")
    public String getBooksFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);

        model.addAttribute("books", bookService.getAllBooks()); // Kitap listesi için veri çek
        return "fragments/list-books-content :: contentFragment";
    }

    @GetMapping("/employee/fragments/books/add")
    public String getAddBookFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book()); // Yeni kitap objesi
        model.addAttribute("authors", authorService.findAll()); // Yazar listesi
        model.addAttribute("publishers", publisherService.findAll()); // Yayıncı listesi
        return "fragments/add-book-content :: contentFragment";
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


    @GetMapping("/employee/fragments/members")
    public String getMembersFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers()); // Tüm kullanıcıları çek (üyeleri)
        return "fragments/list-user :: contentFragment"; // Üye listesi için varsayılan fragment
    }

    @GetMapping("/employee/fragments/members/add")
    public String getAddMemberFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        // Üye kayıt formu için başlangıç verileri gerekirse eklenebilir.
        model.addAttribute("newUser", new User());
        return "fragments/member-registration :: contentFragment";
    }

    @PostMapping("/api/members/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerMember(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            response.put("success", false);
            response.put("message", "Yetkisiz işlem");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            user.setRole("USER");
            User savedUser = userService.saveUser(user);
            response.put("success", true);
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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
}