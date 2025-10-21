package com.example.libraryrevision;

import com.example.libraryrevision.DTOs.FilterDTO;
import com.example.libraryrevision.library.Book;
import com.example.libraryrevision.services.book.BookRepository;
import com.example.libraryrevision.services.book.BookService;
import com.example.libraryrevision.services.book.BookSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(BookService.class)
class BookRepositorySpecTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void pgProps(DynamicPropertyRegistry r) {
        if (!POSTGRES.isRunning()) POSTGRES.start();
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
        r.add("spring.flyway.enabled", () -> true);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired BookRepository repo;

    @BeforeEach
    void seed() {
        repo.save(new Book(null, "Clean Code", "9780132350884", 3,
                LocalDateTime.parse("2008-08-01T00:00:00"), "Robert C. Martin"));
        repo.save(new Book(null, "Effective Java", "9780134685991", 2,
                LocalDateTime.parse("2018-01-06T00:00:00"), "Joshua Bloch"));
        repo.save(new Book(null, "Грокаем алгоритмы", "9785001008491", 0,
                LocalDateTime.parse("2017-05-01T00:00:00"), "Адитья Бхаргава"));
    }

    @Test @DisplayName("byFilter(q): search by substring in title/author/isbn/stock/date")
    void searchByFreeText() {
        var spec = BookSpecs.byFilter("java");
        var found = repo.findAll(spec);
        assertThat(found).extracting(Book::getTitle)
                .contains("Effective Java")
                .doesNotContain("Clean Code");
    }

    @Test @DisplayName("byFilters(...): availability and date interval")
    void searchByMultipleFilters() {
        var from = LocalDateTime.parse("2017-12-31T00:00:00");
        var to = LocalDateTime.parse("2018-12-31T23:59:59");

        var spec = BookSpecs.byFilters(
                null, null, null,
                true,           // only available (stock > 0)
                from, to
        );

        List<Book> found = repo.findAll(spec);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Autowired BookService service;

    @Test @DisplayName("search(...): order of books, no filtering")
    void sortingBooks(){
        //by Date in ascending order, expecting Books id's: 1, 3, 2
        var sortingDTO1 = new FilterDTO(null, null, null, false, null, null, "DATE", true);

        List<Book> received = service.search(sortingDTO1);

        assertThat(received.stream().map(Book::getTitle).collect(Collectors.toList())).isEqualTo(List.of("Clean Code", "Грокаем алгоритмы", "Effective Java"));
        //by Stock in descending order, expecting Books id's: 1, 2, 3
        var sortingDTO2 = new FilterDTO(null, null, null, false, null, null, "stock", false);

        List<Book> received2 = service.search(sortingDTO2);

        assertThat(received2.stream().map(Book::getTitle).collect(Collectors.toList())).isEqualTo(List.of("Clean Code", "Effective Java", "Грокаем алгоритмы"));

    }
}
