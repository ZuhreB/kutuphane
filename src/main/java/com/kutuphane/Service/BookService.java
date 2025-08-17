package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import com.kutuphane.Repository.BookRepository;
import com.kutuphane.Repository.BorrowRepository;
import com.kutuphane.Repository.UserRepository;
// It's better to use Spring's Transactional annotation for better integration.
import org.springframework.transaction.annotation.Transactional;
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

    // @Autowired is redundant on a single constructor in modern Spring versions.
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
        book.setAvailableCopies(book.getAvailableCopies() - 1); // This change will be saved automatically on transaction commit.

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setReturnDate(LocalDateTime.now().plusDays(14)); // 14-day borrow period
        borrow.setStatus("BORROWED");
        borrowRepository.save(borrow);
    }
    public Book saveBook(Book book){
        bookRepository.save(book) ;
        return book;
    }

    @Transactional
    public void deleteBook(Long id) {
        // 1. Check if the book exists to provide a clear error message.
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Silinecek kitap bulunamadı. ID: " + id);
        }

        // 2. Check if the book is currently borrowed by anyone.
        if (borrowRepository.existsByBook_BookIDAndStatus(id, "BORROWED")) {
            // If it is, throw an exception to stop the deletion.
            throw new IllegalStateException("Bu kitap şu anda bir üyede ödünç olduğu için silinemez.");
        }

        // 3. If the book is not currently borrowed, delete all its past borrow records
        // to avoid the foreign key constraint error.
        borrowRepository.deleteByBook_BookID(id);

        // 4. Now that all references are gone, it's safe to delete the book itself.
        bookRepository.deleteById(id);
    }
}
