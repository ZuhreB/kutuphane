package com.kutuphane.Controller;

import com.kutuphane.Entity.*;
import com.kutuphane.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;
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


        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks()); // Eğer panelde kitap listesi varsa
        return "fragments/main-content :: contentFragment";
    }
    @GetMapping("/employee/fragments/employee/add")
    public String getAddMemberFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("newUser", new User());
        System.out.println("member registrationa gitmek üzere");
        return "fragments/employee-registration :: contentFragment";

    }
    @PostMapping("/api/employee/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerMember(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || (!"ADMIN".equals(loggedUser.getRole()))) {
            response.put("success", false);
            response.put("message", "Yetkisiz işlem");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            user.setRole("EMPLOYEE");
            System.out.println(user.getRole()+"eklenen role");
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

    }