package com.kutuphane.Controller;

import com.kutuphane.Entity.Author;
import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Publisher;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.AuthorService;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.PublisherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AdminController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/admin/page")
    public String showAdminPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Admin Paneli");
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("contentFragment", "fragments/admin-content :: admin-content");

        return "layout"; // Artık layout.html şablonunu kullanıyor
    }



    @PostMapping("/admin/books/delete/{bookID}")
    public String deleteBook(@PathVariable Long bookID, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied.");
            return "redirect:/login";
        }

        try {
            bookService.deleteBook(bookID);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting book: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/page";
    }
}