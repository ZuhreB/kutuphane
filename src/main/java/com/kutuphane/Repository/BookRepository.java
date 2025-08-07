package com.kutuphane.Repository;

import com.kutuphane.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.publisher WHERE lower(b.title) LIKE lower(concat('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query("SELECT b FROM Book b JOIN FETCH b.author a JOIN FETCH b.publisher WHERE lower(a.firstName) LIKE lower(concat('%', :authorName, '%')) OR lower(a.lastName) LIKE lower(concat('%', :authorName, '%'))")
    List<Book> findByAuthor_FirstNameContainingIgnoreCase(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.publisher WHERE lower(b.topic) LIKE lower(concat('%', :topic, '%'))")
    List<Book> findByTopicContainingIgnoreCase(@Param("topic") String topic);

    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.publisher WHERE b.isbn = :isbn")
    List<Book> findByIsbnIgnoreCase(@Param("isbn") String isbn);
}