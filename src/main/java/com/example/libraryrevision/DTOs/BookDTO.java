package com.example.libraryrevision.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Valid
@Data
public class BookDTO {
    @NotEmpty
    private String title;
    private String isbn;
    private Integer stock;
    private LocalDateTime published_at;
    private String author;
}
