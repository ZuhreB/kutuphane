package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BookRepository;
import com.kutuphane.Repository.BorrowRepository;
import com.kutuphane.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public Borrow lendBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Kitap stokta yok");
        }

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now().atStartOfDay());
        borrow.setReturnDate(LocalDate.now().plusDays(14).atStartOfDay());
        borrow.setStatus("BORROWED");

        // Kitabın mevcut kopyasını güncelle
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        System.out.println("saveeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return borrowRepository.save(borrow);
    }

    public List<Borrow> getBorrowedBooks() {
        return borrowRepository.findBorrowedBooks();
    }
    public List<Borrow> findBorrowHistoryByUserId(Long userId) {
        return borrowRepository.findByUser_UserIDOrderByBorrowDateDesc(userId);
    }

    @Transactional
    public void returnBook(Long borrowId) {
        // 1. Ödünç kaydını bul, bulamazsan hata fırlat
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new EntityNotFoundException("Ödünç kaydı bulunamadı: " + borrowId));

        // 2. Kitap zaten iade edilmişse, tekrar işlem yapmayı engelle.
        if ("RETURNED".equals(borrow.getStatus())) {
            throw new IllegalStateException("Bu kitap zaten iade edilmiş.");
        }

        // 3. Ödünç kaydını güncelle.
        borrow.setStatus("RETURNED");
        borrow.setActualReturnDate(LocalDate.now().atStartOfDay());

        // 4. Kitabın stok sayısını 1 artır.
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

}