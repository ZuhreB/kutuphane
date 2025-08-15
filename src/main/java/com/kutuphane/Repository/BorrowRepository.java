package com.kutuphane.Repository;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    // Belirli bir kitap için, henüz iade edilmemiş (returnDate IS NULL) ve
    // beklenen iade tarihine göre sıralanmış ödünç alma kayıtlarını bulur.
    List<Borrow> findByBookBookIDAndActualReturnDateIsNullOrderByReturnDateAsc(Long bookId);
    @Query("SELECT b FROM Borrow b JOIN FETCH b.book JOIN FETCH b.user WHERE b.status = 'BORROWED'")
    List<Borrow> findBorrowedBooks();
    }