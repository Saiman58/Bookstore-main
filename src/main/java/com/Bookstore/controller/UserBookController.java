package com.Bookstore.controller;


import com.Bookstore.dto.mapper.UserBookMapper;
import com.Bookstore.dto.request.AddBookRequest;
import com.Bookstore.dto.request.ReviewRequest;
import com.Bookstore.dto.response.UserBookResponse;
import com.Bookstore.model.entity.UserBook;
import com.Bookstore.model.enums.ReadingStatus;
import com.Bookstore.service.UserBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/books")
@RequiredArgsConstructor
public class UserBookController {

    private final UserBookService userBookService;

    //POST /api/users/{userId}/books/{bookId}?status=WANT_TO_READ
    // Добавить книгу в свою библиотеку
    @PostMapping
    public ResponseEntity<UserBookResponse> addBookToUser(
            @PathVariable Long userId,
            @Valid @RequestBody AddBookRequest request) {

        UserBookResponse response = userBookService.addBookToUser(
                userId,
                request.getBookId(),
                request.getStatus()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/users/{userId}/books - все книги пользователя
    // GET /api/users/{userId}/books?status=READING - только по статусу
    @GetMapping
    public ResponseEntity<List<UserBookResponse>> getUserBooks(
            @PathVariable Long userId,
            @RequestParam(required = false) ReadingStatus status) {

        List<UserBookResponse> responses = (status != null)
                ? userBookService.getUserBooksByStatus(userId, status)
                : userBookService.getUserBooks(userId);

        return ResponseEntity.ok(responses);
    }

    // GET /api/users/{userId}/books/{bookId} - получить конкретную книгу из библиотеки
    @GetMapping("/{bookId}")
    public ResponseEntity<UserBookResponse> getUserBook(
            @PathVariable Long userId,
            @PathVariable Long bookId) {

        UserBookResponse response = userBookService.getUserBook(userId, bookId);
        return ResponseEntity.ok(response);
    }

    // PATCH /api/users/{userId}/books/{bookId}/status?status=READING
    // Изменить статус книги
    @PatchMapping("/{bookId}/status")
    public ResponseEntity<UserBookResponse> updateBookStatus(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestParam ReadingStatus status) {

        UserBookResponse response = userBookService.updateBookStatus(userId, bookId, status);
        return ResponseEntity.ok(response);
    }

    // POST /api/users/{userId}/books/{bookId}/review
    // Поставить оценку и оставить отзыв
    @PostMapping("/{bookId}/review")
    public ResponseEntity<UserBookResponse> addReview(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request) {

        UserBookResponse response = userBookService.rateAndReviewBook(
                userId,
                bookId,
                request.getRating(),
                request.getReview()
        );

        return ResponseEntity.ok(response);
    }


    // DELETE /api/users/{userId}/books/{bookId}
    // Удалить книгу из библиотеки
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBookFromUser(
            @PathVariable Long userId,
            @PathVariable Long bookId) {

        userBookService.removeBookFromUser(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/users/{userId}/books/stats - статистика по чтению
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getReadingStats(@PathVariable Long userId) {
        Map<String, Long> stats = Map.of(
                "wantToRead", userBookService.countUserBooksByStatus(userId, ReadingStatus.WANT_TO_READ),
                "reading", userBookService.countUserBooksByStatus(userId, ReadingStatus.READING),
                "read", userBookService.countUserBooksByStatus(userId, ReadingStatus.READ),
                "abandoned", userBookService.countUserBooksByStatus(userId, ReadingStatus.ABANDONED)
        );
        return ResponseEntity.ok(stats);
    }
}


