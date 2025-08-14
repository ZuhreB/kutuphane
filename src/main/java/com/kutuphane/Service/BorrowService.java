package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Repository.BorrowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BorrowService {
    BorrowRepository borrowRepository;
    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }
    public List<Book> getBorrowedBooks() {
        return borrowRepository.findBorrowedBooks();
    }
}
