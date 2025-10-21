package com.example.libraryrevision.DTOs;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Valid
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
    private String title;
    private String author;
    private String isbn;
    private boolean checkAvail;
    private LocalDateTime yearFrom;
    private LocalDateTime yearTo;
    private String sortBy;
    private boolean ascending;
}
