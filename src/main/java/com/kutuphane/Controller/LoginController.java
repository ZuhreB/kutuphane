package com.kutuphane.Controller;

import com.kutuphane.Entity.User;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/login"})
    public String showLoginPage() {
        return "login-page";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> Login(@RequestBody User loginData, HttpSession session) {
        User loggedUser = userService.loginUser(loginData.getUsername(), loginData.getPassword());

        if (loggedUser != null) {
            session.setAttribute("loggedUser", loggedUser);
            if ("ADMIN".equals(loggedUser.getRole())) {
                return ResponseEntity.ok("/admin/page");
            }
            return ResponseEntity.ok("/main");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        // Mevcut oturumu geçersiz kılarak kullanıcıyı sistemden çıkarır.
        session.invalidate();
        return "redirect:/login";
    }
}