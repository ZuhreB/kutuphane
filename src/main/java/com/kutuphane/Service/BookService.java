package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BookRepository;
import com.kutuphane.Repository.BorrowRepository;
import com.kutuphane.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    @Autowired
    public BookService(BookRepository bookRepository, BorrowRepository borrowRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    public List<Book> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return Collections.emptyList();
        }
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    public List<Book> searchByAuthor(String authorName){
        if(authorName==null || authorName.isBlank()){
            return Collections.emptyList();
        }
        return bookRepository.findByAuthor_FirstNameContainingIgnoreCase(authorName);
    }
    public List<Book> searchByTopic(String topic){
        if(topic==null || topic.isBlank()){
            return Collections.emptyList();
        }
        return bookRepository.findByTopicContainingIgnoreCase(topic);
    }

    public List<Book> searchByISBN(String isbn){
        if(isbn==null || isbn.isBlank()){
            return Collections.emptyList();
        }
        return bookRepository.findByIsbnIgnoreCase(isbn);
    }



    @Transactional
    public void borrowBook(Long bookId, String username) {
        // 1. Kitabı ve kullanıcıyı veritabanından bul.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found with id: " + bookId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found with username: " + username));

        // 2. Kitabın mevcut olup olmadığını kontrol et.
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("This book is currently unavailable.");
        }

        // 3. Kitabın mevcut kopya sayısını bir azalt.
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 4. Yeni bir ödünç alma kaydı oluştur.
        Borrow newBorrow = new Borrow();
        newBorrow.setBook(book);
        newBorrow.setUser(user);
        newBorrow.setBorrowDate(LocalDateTime.now());
        newBorrow.setReturnDate(LocalDateTime.now().plusDays(14)); // 14 gün sonrası için teslim tarihi
        newBorrow.setStatus("BORROWED");

        borrowRepository.save(newBorrow);
    }
}
