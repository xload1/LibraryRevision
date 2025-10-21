package com.example.libraryrevision.services.book;

import com.example.libraryrevision.DTOs.BookDTO;
import com.example.libraryrevision.DTOs.FilterDTO;
import com.example.libraryrevision.library.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
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

    @Transactional
    @CacheEvict(cacheNames = "books", key = "#id")
    public Optional<Book> updateBook(Long id, BookDTO newBook) {
        return bookRepository.findById(id).map(b -> {
            b.setTitle(newBook.getTitle());
            b.setIsbn(newBook.getIsbn());
            b.setStock(newBook.getStock());
            b.setPublished_at(newBook.getPublished_at());
            b.setAuthor(newBook.getAuthor());
            return b;
        });
    }

    public enum DeleteResult {DELETED, NOT_FOUND, CONFLICT;}
    @Transactional
    @CacheEvict(cacheNames = "books", key = "#id")
    public DeleteResult deleteBook(Long id){
        try {
            bookRepository.deleteById(id);
            return DeleteResult.DELETED;
        } catch (EmptyResultDataAccessException e) {
            return DeleteResult.NOT_FOUND;
        } catch (DataIntegrityViolationException e) {
            return DeleteResult.CONFLICT;
        }
    }
    @Cacheable(cacheNames = "allBooks")
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
    public List<Book> getFilteredBooks(Specification<Book> spec){
        return bookRepository.findAll(spec);
    }
    public Page<Book> getFilteredBooks(Specification<Book> spec, Pageable pageable){
        return bookRepository.findAll(spec, pageable);
    }

    public List<Book> search(FilterDTO filters){
        List<Book> filtered = getFilteredBooks(
                BookSpecs.byFilters(
                        filters.getTitle(),
                        filters.getAuthor(),
                        filters.getIsbn(),
                        filters.isCheckAvail(),
                        filters.getYearFrom(),
                        filters.getYearTo()
                )
        );

        if (filters.getSortBy() != null && !filters.getSortBy().isBlank())
            filtered.sort(sortBy(filters.getSortBy(), filters.isAscending()));

        return filtered;
    }
    public Comparator<Book> sortBy(String criteria, boolean asc){
        char criteriaChar = Character.toLowerCase(criteria.trim().charAt(0));

        Comparator<Book> cmp = switch (criteriaChar) {
            case 't' -> Comparator.comparing(Book::getTitle,
                    Comparator.nullsLast(String::compareToIgnoreCase));
            case 'a' -> Comparator.comparing(Book::getAuthor,
                    Comparator.nullsLast(String::compareTo));
            case 'i' -> Comparator.comparing(Book::getIsbn,
                    Comparator.nullsLast(String::compareTo));
            case 's' -> Comparator.comparing(Book::getStock,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case 'd' -> Comparator.comparing(Book::getPublished_at,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Book::getId);
        };

        if (!asc) cmp = cmp.reversed();
        return cmp;
    }
}
