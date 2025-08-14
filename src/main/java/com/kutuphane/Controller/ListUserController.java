package com.kutuphane.Controller;

import com.kutuphane.Entity.User;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListUserController {
    private final UserService userService;

    public ListUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/employee/members")
    public String listUser(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole().toUpperCase()) && !"ADMIN".equals(loggedUser.getRole().toUpperCase()))) {
            return "redirect:/login";
        }
        System.out.println("hey");
        model.addAttribute("pageTitle", "Kütüphane Üyeleri");
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("contentFragmentName", "fragments/list-user.html :: list-user"); // list-books.html fragment'ını kullan

        return "layout";
    }
}

