package com.example.libraryrevision.services;

import com.example.libraryrevision.library.Book;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookSpecs{
    //Blank or empty strings will not affect filtering
    public static boolean strEmpty(String s) {
        return s == null || s.isBlank() || s.isEmpty();
    }
    public static Specification<Book> byFilters(String title, String author, String isbn, boolean check, LocalDateTime publishedFrom, LocalDateTime publishedUpto) {
        return Specification.allOf(eqTitle(title), eqAuthor(author), eqIsbn(isbn), checkAvail(check), dateGeq(publishedFrom), dateLeq(publishedUpto));
    }
    public static Specification<Book> byFilter(String q){
        return (root, query, builder) ->  strEmpty(q) ? builder.conjunction() : builder.or(
                builder.like(builder.lower(root.get("title")), "%"+q.toLowerCase()+"%"),
                builder.like(builder.lower(root.get("author")), "%"+q.toLowerCase()+"%"),
                builder.like(builder.lower(root.get("isbn")),  "%"+q.toLowerCase()+"%"),
                builder.like(builder.toString(root.get("stock")),  "%"+q+"%"),
                builder.like(builder.function("to_char", String.class,
                        root.get("published_at"), builder.literal("HH24:MI DD.MM.YYYY")),  "%"+q+"%")
        );
    }
    static Specification<Book> eqTitle(String title){
        return (root, query, builder) -> strEmpty(title) ? builder.conjunction() :
                builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
    static Specification<Book> eqAuthor(String author){
        return (root, query, builder) -> strEmpty(author) ? builder.conjunction() :
                builder.like(builder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }
    static Specification<Book> eqIsbn(String isbn){
        return (root, query, builder) -> strEmpty(isbn) ? builder.conjunction() :
                builder.like(builder.lower(root.get("isbn")), "%" + isbn.toLowerCase() + "%");
    }
    static Specification<Book> checkAvail(boolean check){
        return (root, query, builder) -> !check ? builder.conjunction():
                builder.greaterThan(root.get("stock"), 0);
    }
    static Specification<Book> dateGeq(LocalDateTime dt){
        return (root, query, builder) -> dt == null ? builder.conjunction() :
                builder.greaterThanOrEqualTo(root.get("published_at"), dt);
    }
    static Specification<Book> dateLeq(LocalDateTime dt){
        return (root, query, builder) -> dt == null ? builder.conjunction() :
                builder.lessThanOrEqualTo(root.get("published_at"), dt);
    }
}
