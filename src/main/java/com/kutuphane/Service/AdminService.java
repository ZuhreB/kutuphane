package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Repository.BookRepository;
import com.kutuphane.Repository.UserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Collections;
import java.util.List;

public class AdminService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    AdminService(BookRepository bookRepository, UserRepository userRepository){
        this.bookRepository=bookRepository;
        this.userRepository=userRepository;
    }
    List<Book> deleteBook(){
        return Collections.emptyList();
    }
}
