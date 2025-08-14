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
import org.springframework.stereotype.Controller; // Change to @Controller
import org.springframework.ui.Model; // Import Model
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller // Change this from @RestController to @Controller
@RequestMapping("/employee/books") // Adjusted base request mapping
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService; // Inject AuthorService
    private final PublisherService publisherService; // Inject PublisherService

    @Autowired
    public BookController(BookService bookService, AuthorService authorService, PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole().toUpperCase())) {
            return "redirect:/login"; // Redirect if not logged in or not an employee
        }

        model.addAttribute("pageTitle", "Kitap Ekle");
        model.addAttribute("book", new Book()); // Create a new Book object for the form
        model.addAttribute("authors", authorService.findAll()); // Get all authors
        model.addAttribute("publishers", publisherService.findAll()); // Get all publishers
        model.addAttribute("loggedUser", loggedUser); // Pass logged user to layout
        model.addAttribute("contentFragment", "fragments/add-book-content.html :: add-book-content");

        return "layout";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book, Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole().toUpperCase())) {
            return "redirect:/login";
        }

        book.setAvailableCopies(book.getTotalCopies());

        book.setAddedDate(LocalDateTime.now());

        Author author = authorService.getAuthorById(book.getAuthor().getAuthorID());
        Publisher publisher = publisherService.getPublisherById(book.getPublisher().getPublisherID());

        book.setAuthor(author);
        book.setPublisher(publisher);

        bookService.saveBook(book); // Save the book

        return "redirect:/employee/books"; // Assuming you have a /employee/books endpoint
    }

    @GetMapping
    public String listBooks(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole().toUpperCase())) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Kitap Yönetimi");
        model.addAttribute("books", bookService.getAllBooks()); // Tüm kitapları getir
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("contentFragmentName", "fragments/list-books-content.html :: list-books-content"); // list-books.html fragment'ını kullan

        return "layout"; // layout.html'i render et
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"EMPLOYEE".equals(loggedUser.getRole().toUpperCase())) {
            return "redirect:/login";
        }

        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Kitap başarıyla silindi.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Kitap silinirken bir hata oluştu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "layout";
    }
    }