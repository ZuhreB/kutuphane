package com.kutuphane.Repository;

import com.kutuphane.Entity.Book;
import com.kutuphane.Entity.Borrow;
import com.kutuphane.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByBook(Book book);
    List<Borrow> findByUser(User user);
    List<Borrow> findByUserAndBook(User user, Book book);
}