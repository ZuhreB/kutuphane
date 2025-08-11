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

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Kitap bulunamadı!"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Bu kitap şu anda mevcut değil!");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Kullanıcı bulunamadı!"));

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setReturnDate(LocalDateTime.now().plusDays(14));
        borrow.setStatus("BORROWED");
        borrowRepository.save(borrow);
    }
    public void saveBook(Book book){
        bookRepository.save(book);
    }
    public void deleteBook(Book book){
        bookRepository.delete(book);
    }
    public void deleteBook(Long id){
        bookRepository.deleteById(id);
    }
    public List<Book> findById(Long bookid){
        return bookRepository.findAll();

    }
}
