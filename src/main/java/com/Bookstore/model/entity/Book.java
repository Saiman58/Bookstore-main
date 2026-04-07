package com.Bookstore.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;           // название книги

    @Column(nullable = false)
    private String author;          // автор

    @Column(length = 1000)
    private String description;     // описание (аннотация)

    private String isbn;            // международный номер книги (уникальный)

    @Column(name = "page_count")
    private Integer pageCount;      // количество страниц

    @Column(name = "published_date")
    private LocalDate publishedDate; // дата публикации

    private String publisher;        // издательство

    @Column(name = "cover_image_url")
    private String coverImageUrl;    // ссылка на обложку

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
