package com.kutuphane.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminControleer {
    @GetMapping("/Admin")
    public String login() {
        return "admin-page";
    }
}
