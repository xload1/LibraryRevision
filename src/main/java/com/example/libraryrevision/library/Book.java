package com.example.libraryrevision.library;

import com.example.libraryrevision.DTOs.BookDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.cache.annotation.EnableCaching;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String isbn;
    private Integer stock;
    private LocalDateTime published_at;
    private String author;

    public Book (BookDTO bookDTO){
        this.title = bookDTO.getTitle();
        this.isbn = bookDTO.getIsbn();
        this.stock = bookDTO.getStock();
        this.published_at = bookDTO.getPublished_at();
        this.author = bookDTO.getAuthor();
    }
}
