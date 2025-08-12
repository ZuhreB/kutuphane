package com.kutuphane.Controller;

import com.kutuphane.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        // *** BU SATIRI EKLEYİN ***
        System.out.println("Giriş Yapan Kullanıcının Rolü: " + loggedUser.getRole());

        model.addAttribute("loggedUser", loggedUser); // Giriş yapmış kullanıcıyı modele ekle
        model.addAttribute("pageTitle", "Kütüphane Ana Sayfası");

        // Main content fragment'ı hala aynı şekilde gönderiyoruz
        model.addAttribute("contentFragment", "fragments/main-content :: contentFragment");
        return "layout"; // Yeni layout şablonunu döndür
    }
}