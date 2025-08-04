package com.kutuphane.Service;

import com.kutuphane.Entity.Admin;
import com.kutuphane.Entity.Book;
import com.kutuphane.Repository.AdminRepository;
import com.kutuphane.Repository.BookRepository;

import java.util.List;

public class AdminService {
    private final AdminRepository adminRepository;
    private final BookRepository bookRepostoriy;

    public AdminService(AdminRepository adminRepository, BookRepository bookRepostoriy) {
        this.adminRepository = adminRepository;
        this.bookRepostoriy = bookRepostoriy;
    }

    public List<Book> getAllTodos() {
        return BookRepository.findAll();
    }
    public Book ShowAllBook() {

    }
    }



