package com.example.libraryrevision;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LibraryRevisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryRevisionApplication.class, args);
    }

}
