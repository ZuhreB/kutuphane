package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.AuthorService;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.PublisherService;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/employee/page")
    public String showAdminPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Çalışan Paneli");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("contentFragment", "fragments/employee-content.html :: employee-content.html");

        return "layout"; // Artık layout.html şablonunu kullanıyor
    }
    @GetMapping("/employee/members/add")
    public String showAddMemberForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        System.out.println("girdim de ...");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Yeni Üye Ekle");
        model.addAttribute("loggedUser", loggedUser);
        // Load the new member registration fragment
        model.addAttribute("contentFragment", "fragments/member-registration.html :: member-registration");
        return "layout";
    }

    // This method will handle the form submission for adding a new member
    @PostMapping("/api/members/register") // Changed endpoint to be more specific to members
    @ResponseBody // Indicates that the return value should be bound to the web response body
    public User registerMember(@RequestBody User user) { // Expects a User object in the request body
        // Set the role for the new member (e.g., "USER" or "MEMBER")
        user.setRole("USER"); // Or "MEMBER", depending on your role definition
        // Save the new user (member) to the database using your UserService
        userService.saveUser(user);
        return user; // Return the saved user object (e.g., with generated ID)
    }
}


