package com.kutuphane.Repository;

import com.kutuphane.Entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    public List<Author> findAll();
}
