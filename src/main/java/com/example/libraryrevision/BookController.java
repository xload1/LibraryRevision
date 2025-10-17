package com.example.libraryrevision;

import com.example.libraryrevision.DTOs.BookDTO;
import com.example.libraryrevision.library.Book;
import com.example.libraryrevision.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> postBook(@RequestBody @Valid BookDTO dto){
        Book book = new Book(dto);
        Book saved = bookService.addBook(book);
        return ResponseEntity.ok(saved);
    }
}
