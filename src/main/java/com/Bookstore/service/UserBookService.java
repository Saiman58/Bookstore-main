package com.Bookstore.service;

import com.Bookstore.dto.response.UserBookResponse;
import com.Bookstore.model.entity.UserBook;
import com.Bookstore.model.enums.ReadingStatus;

import java.util.List;

public interface UserBookService {

    // Добавить книгу в библиотеку пользователя
    UserBookResponse addBookToUser(Long userId, Long bookId, ReadingStatus status);

    // Изменить статус книги у пользователя
    UserBookResponse updateBookStatus(Long userId, Long bookId, ReadingStatus newStatus);

    // Поставить оценку и написать отзыв
    UserBookResponse rateAndReviewBook(Long userId, Long bookId, Integer rating, String review);

    // Получить все книги пользователя
    List<UserBookResponse> getUserBooks(Long userId);

    // ПОЛУЧИТЬ КОНКРЕТНУЮ КНИГУ
    UserBookResponse getUserBook(Long userId, Long bookId);

    //Получить книги пользователя по статусу
    List<UserBookResponse> getUserBooksByStatus(Long userId, ReadingStatus status);

    // Удалить книгу из библиотеки пользователя
    void removeBookFromUser(Long userId, Long bookId);

    //Посчитать количество книг пользователя по статусу
    long countUserBooksByStatus(Long userId, ReadingStatus status);
}
