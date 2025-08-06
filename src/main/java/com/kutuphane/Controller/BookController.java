package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search/by-title")
    public String searchByTitle(@RequestParam("query")String title ,
                                     HttpSession session ,
                                     Model model) {
        if(!prepareModelWithUserData(session,model)){
            return "redirect:/login";
        }
        List<Book> books = bookService.searchByTitle(title);
        model.addAttribute("books", books);
        model.addAttribute("lastQuery", title);
        model.addAttribute("lastType", "title");
        return "main-page";
    }

    @GetMapping("/search/by-author")
    public String searchByAuthor(@RequestParam("query") String author ,
                                 HttpSession session,
                                 Model model){
        if(!prepareModelWithUserData(session,model)){
            return "redirect:/login";
        }
        List<Book> books = bookService.searchByAuthor(author);
        model.addAttribute("books",books);
        model.addAttribute("lastQuery",author);
        model.addAttribute("lastType","author");
        return "main-page";

    }

    @GetMapping("/search/by-isbn")
    public String searchWithISBN(@RequestParam("query")String isbn,
                                 HttpSession session,Model model){
        if(!prepareModelWithUserData(session,model)){
            return "redirect:/login";
        }
        Optional<Book> bookOptional = bookService.searchByISBN(isbn); // Get the Optional
        List<Book> books=bookOptional.map(List::of ).orElse(Collections.emptyList());
        model.addAttribute("books", books); // Now "books" is always a List<Book>
        model.addAttribute("lastQuery",isbn);
        model.addAttribute("lastType","isbn");

        return "main-page";

    }

    private boolean prepareModelWithUserData(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return false;
        }
        model.addAttribute("userName", loggedInUser.getFirstName());
        return true;
    }
}