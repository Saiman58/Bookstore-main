package com.Bookstore.repository;

import com.Bookstore.dto.response.UserBookResponse;
import com.Bookstore.dto.response.UserResponse;
import com.Bookstore.model.entity.User;
import com.Bookstore.model.entity.UserBook;
import com.Bookstore.model.enums.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    // Все книги конкретного пользователя
    List<UserBook> findByUser(User user);

    // Книги пользователя с конкретным статусом
    List<UserBook> findByUserAndStatus(User user, ReadingStatus status);

    // Проверка, есть ли уже такая книга у пользователя
    Optional<UserBook> findByUserAndBookId(User user, Long bookId);

    // Сколько книг у пользователя с определенным статусом
    long countByUserAndStatus(User user, ReadingStatus status);

    //Проверить существование книги у пользователя
    boolean existsByUserAndBookId(User user, Long bookId);
}
