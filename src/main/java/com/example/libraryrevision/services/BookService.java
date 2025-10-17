package com.example.libraryrevision.services;

import com.example.libraryrevision.library.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Cacheable(cacheNames = "books", key = "#id")
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @CacheEvict(cacheNames = "books", key = "#result.id", condition = "#result != null")
    public Book addBook(Book book){
        return bookRepository.save(book);
    }
}
