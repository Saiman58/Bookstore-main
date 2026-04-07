package com.Bookstore.model.entity;

import com.Bookstore.model.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_books")
@NoArgsConstructor
@AllArgsConstructor
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;              // ссылка на пользователя

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;              // ссылка на книгу

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status = ReadingStatus.WANT_TO_READ;  // статус

    @Column(name = "started_at")
    private LocalDateTime startedAt;    // когда начал читать

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;   // когда закончил

    @Column(name = "user_rating")
    private Integer userRating;         // оценка (1-5)

    @Column(length = 1000)
    private String userReview;          // отзыв

    @Column(name = "added_at")
    private LocalDateTime addedAt;       // когда добавил в свою библиотеку

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }
}
