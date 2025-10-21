package com.example.libraryrevision;

import com.example.libraryrevision.DTOs.BookDTO;
import com.example.libraryrevision.DTOs.FilterDTO;
import com.example.libraryrevision.library.Book;
import com.example.libraryrevision.services.book.BookService;
import com.example.libraryrevision.services.book.BookSpecs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
        Book saved = bookService.addBook(new Book(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody @Valid BookDTO dto, @PathVariable Long id) {
        return bookService.updateBook(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long id){
        return switch (bookService.deleteBook(id)) {
            case DELETED  -> ResponseEntity.noContent().build();      // 204
            case NOT_FOUND -> ResponseEntity.notFound().build();      // 404
            case CONFLICT -> ResponseEntity.status(409).build();
        };
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
