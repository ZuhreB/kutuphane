package com.kutuphane.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/User")
    public String login() {
        return "User-page";
    }
}
