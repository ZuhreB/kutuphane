package com.kutuphane.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String login() {
        return "login-page";
    }
    @GetMapping("/register")
    public String register() {
        return "register-page";
    }
}
