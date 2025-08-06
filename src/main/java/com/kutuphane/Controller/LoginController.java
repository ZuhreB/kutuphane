package com.kutuphane.Controller;

import com.kutuphane.Entity.User;
import com.kutuphane.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String ControlLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

     User loggedUser = userService.loginUser(username,password);
     if(loggedUser!=null){
         session.setAttribute("loggedUser", loggedUser);
                 return "redirect:/main";
     }
        // 5. Giriş başarısızsa, login sayfasına hata mesajı vercem
        model.addAttribute("loginError", "Invalid username or password.");
        return "login-page";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        // Sessiondaki kullanıcıyı siliyorum ne olur ne olmaz karışıklık çıkmasın diye
        session.invalidate();
        return "redirect:/login";
    }
}