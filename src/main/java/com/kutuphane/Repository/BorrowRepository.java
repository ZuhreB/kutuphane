package com.kutuphane.Repository;

import com.kutuphane.Entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    // Belirli bir kitap için, henüz iade edilmemiş (returnDate IS NULL) ve
    // beklenen iade tarihine göre sıralanmış ödünç alma kayıtlarını bulur.
    List<Borrow> findByBookBookIDAndActualReturnDateIsNullOrderByReturnDateAsc(Long bookId);

    // DÜZELTİLDİ: User entity'sindeki primary key alanı 'userID' olduğu için bu şekilde güncellendi.
    Optional<Borrow> findByUserUserIDAndBookBookIDAndReturnDateIsNull(Long userId, Long bookId);
}