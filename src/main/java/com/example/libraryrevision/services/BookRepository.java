package com.example.libraryrevision.services;

import com.example.libraryrevision.library.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
