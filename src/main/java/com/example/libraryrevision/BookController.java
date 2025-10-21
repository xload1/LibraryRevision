package com.example.libraryrevision;

import com.example.libraryrevision.DTOs.BookDTO;
import com.example.libraryrevision.DTOs.FilterDTO;
import com.example.libraryrevision.library.Book;
import com.example.libraryrevision.services.BookService;
import com.example.libraryrevision.services.BookSpecs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
    //Get all books filtered with many parameters + sorted in chosen order
    @PostMapping(
            path = "/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Book>> searchBooks(@RequestBody @Valid FilterDTO filters) {
        List<Book> filtered = bookService.search(filters);
        return ResponseEntity.ok(filtered);
    }

    //Simple filters, 1 parameter query search through all variables
    @GetMapping
    public ResponseEntity<Page<Book>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Book> spec = BookSpecs.byFilter(q);

        Page<Book> pageResult = bookService.getFilteredBooks(spec, pageable);
        return ResponseEntity.ok(pageResult);
    }

}
