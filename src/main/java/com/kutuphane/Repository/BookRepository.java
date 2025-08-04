package com.kutuphane.Repository;

import com.kutuphane.Entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String> {
}
