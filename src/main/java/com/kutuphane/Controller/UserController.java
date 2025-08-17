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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Controller
public class UserController {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private BookService bookService;

    public UserController(UserService userService, BorrowService borrowService, BookService bookService) {
        this.userService = userService;
        this.borrowService = borrowService;
        this.bookService=bookService;
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

    @GetMapping("/borrow-history")
    public String getBorrowHistory(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        // Kullanıcı giriş yapmamışsa login sayfasına yönlendir
        if (loggedUser == null) {
            return "redirect:/login";
        }

        // Servis üzerinden kullanıcının ödünç alma geçmişini al
        List<Borrow> userBorrows = borrowService.findBorrowHistoryByUserId(loggedUser.getUserID());
        model.addAttribute("borrows", userBorrows);

        return "fragments/borrow-history-content :: contentFragment";
    }

    @GetMapping("/user/page")
    public String showAdminPage(Model model, HttpSession session) {
        System.out.println("user pagi iiçindeiym");
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"USER".equals(loggedUser.getRole()))) {
            return "redirect:/login"; // Eğer USER değilse login sayfasına yönlendir
        }

        model.addAttribute("pageTitle", "Çalışan Paneli");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks());
        return "layout";
    }






}