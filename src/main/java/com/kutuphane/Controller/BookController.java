package com.kutuphane.Controller;

import com.kutuphane.Entity.Author;
import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Publisher;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import com.kutuphane.Service.PublisherService;
import com.kutuphane.Service.AuthorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // <--- Bu import'u ekleyin
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@Controller
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    @Autowired
    public BookController(BookService bookService, AuthorService authorService,PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService=publisherService;
    }

    @DeleteMapping("/employee/books/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAuthorized(session)) {
            response.put("success", false);
            response.put("message", "Bu işlemi yapmak için yetkiniz yok.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            bookService.deleteBook(id);
            response.put("success", true);
            response.put("message", "Kitap başarıyla silindi (görünümden kaldırıldı)."); // <--- Mesajı güncelleyin
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage()); // BookInUseException'dan gelen mesajı kullanın
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
        } catch (Exception e) {
            // Beklenmeyen diğer tüm hatalar
            response.put("success", false);
            response.put("message", "Kitap silinirken beklenmeyen bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
        }
    }
    @GetMapping("/employee/fragments/books")
    public String getBooksFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);

        model.addAttribute("books", bookService.getAllBooks()); // Kitap listesi için veri çek
        return "fragments/list-books-content :: contentFragment";
    }

    @GetMapping("/employee/fragments/books/add")
    public String getAddBookFragment(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("book", new Book()); // Yeni kitap objesi
        model.addAttribute("authors", authorService.findAll()); // Yazar listesi
        model.addAttribute("publishers", publisherService.findAll()); // Yayıncı listesi
        return "fragments/add-book-content :: contentFragment";
    }
    @PostMapping("/employee/api/books/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBook(@RequestBody Book book, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"EMPLOYEE".equals(loggedUser.getRole()) && !"ADMIN".equals(loggedUser.getRole()))) {
            response.put("success", false);
            response.put("message", "Bu işlemi yapmak için yetkiniz yok.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // Gelen kitabın mevcut kopya sayısını toplam kopya sayısına eşitle
            book.setAvailableCopies(book.getTotalCopies());
            Author realAuthor = authorService.getAuthorById(book.getAuthor().getAuthorID());

            // 2. Fetch the real Publisher entity.
            Publisher realPublisher = publisherService.getPublisherById(book.getPublisher().getPublisherID());
            book.setAuthor(realAuthor);
            book.setPublisher(realPublisher);
            Book savedBook = bookService.saveBook(book);
            response.put("success", true);
            response.put("message", "Kitap başarıyla eklendi: " + savedBook.getTitle());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Kitap eklenirken bir hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private boolean isAuthorized(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && ("EMPLOYEE".equals(loggedUser.getRole()) || "ADMIN".equals(loggedUser.getRole()));
    }
}