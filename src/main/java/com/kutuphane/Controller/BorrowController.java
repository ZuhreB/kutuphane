// Example BorrowController
package com.kutuphane.Controller;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.User;
import com.kutuphane.Service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    private final BookService bookService;

    public BorrowController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("{ıd}")
    public String showBorrowPage(@RequestParam("bookId") Long bookId, Model model) {
        // model.addAttribute("book", book);
        model.addAttribute("borrowedBookId", bookId); // Just an example for now

        return "borrow-page";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getUserById(@PathVariable Long id) {
        Optional<Book> book= bookService.getBookById(id);
        // optinala bir user geldiyse çalışçak yoksa çalışmıycak bu kıısmı
        return book.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}