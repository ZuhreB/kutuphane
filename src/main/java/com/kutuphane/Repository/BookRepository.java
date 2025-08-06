package com.kutuphane.Repository;

import com.kutuphane.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIsbnIgnoreCase(String isbn);
    List<Book> findByTitleContainingIgnoreCase(String title); // Başlıkta arama
    List<Book> findByAuthor_FirstNameContainingIgnoreCase(String name); // Yazar adına göre arama
    List<Book> findByTopicContainingIgnoreCase(String topic);
}