package com.kutuphane.Service;

import com.kutuphane.Entity.Book;
import com.kutuphane.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
}