package com.Bookstore.service;

import com.Bookstore.dto.mapper.UserBookMapper;
import com.Bookstore.dto.response.UserBookResponse;
import com.Bookstore.exception.BookAlreadyAddedException;
import com.Bookstore.exception.BookNotFoundException;
import com.Bookstore.model.entity.Book;
import com.Bookstore.model.entity.User;
import com.Bookstore.model.entity.UserBook;
import com.Bookstore.model.enums.ReadingStatus;
import com.Bookstore.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserBookServiceImpl implements UserBookService {

    private final UserBookRepository userBookRepository;
    private final UserService userService;
    private final BookService bookService;
    private final UserBookMapper userBookMapper;


    // Добавить книгу в библиотеку пользователя
    @Override
    @Transactional
    public UserBookResponse addBookToUser(Long userId, Long bookId, ReadingStatus status) {
        User user = userService.getUserEntityById(userId);
        Book book = bookService.getBookEntityById(bookId);

        var existing = userBookRepository.findByUserAndBookId(user, bookId);
        if (existing.isPresent()) {
            throw new BookAlreadyAddedException("Книга уже есть в библиотеке пользователя");
        }

        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setStatus(status);

        if (status == ReadingStatus.READING) {
            userBook.setStartedAt(LocalDateTime.now());
        }

        UserBook savedUserBook = userBookRepository.save(userBook);
        return userBookMapper.toResponse(savedUserBook);
    }

    // Изменить статус книги у пользователя
    @Override
    @Transactional
    public UserBookResponse updateBookStatus(Long userId, Long bookId, ReadingStatus newStatus) {
        // Получаем Entity
        UserBook userBook = getUserBookEntity(userId, bookId);

        // Логика обновления дат
        if (newStatus == ReadingStatus.READING && userBook.getStartedAt() == null) {
            userBook.setStartedAt(LocalDateTime.now());
        } else if (newStatus == ReadingStatus.READ && userBook.getFinishedAt() == null) {
            userBook.setFinishedAt(LocalDateTime.now());
        } else if (newStatus == ReadingStatus.WANT_TO_READ) {
            userBook.setStartedAt(null);
            userBook.setFinishedAt(null);
        }

        userBook.setStatus(newStatus);
        UserBook updatedUserBook = userBookRepository.save(userBook);

        // Возвращаем DTO
        return userBookMapper.toResponse(updatedUserBook);
    }


    // Поставить оценку и написать отзыв
    @Override
    @Transactional
    public UserBookResponse rateAndReviewBook(Long userId, Long bookId, Integer rating, String review) {
        // Получаем Entity
        UserBook userBook = getUserBookEntity(userId, bookId);

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        userBook.setUserRating(rating);
        userBook.setUserReview(review);

        UserBook updatedUserBook = userBookRepository.save(userBook);

        // Возвращаем DTO
        return userBookMapper.toResponse(updatedUserBook);
    }

    // Получить все книги пользователя
    @Override
    public List<UserBookResponse> getUserBooks(Long userId) {
        User user = userService.getUserEntityById(userId);

        return userBookRepository.findByUser(user)
                .stream()
                .map(userBookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserBookResponse getUserBook(Long userId, Long bookId) {
        UserBook userBook = getUserBookEntity(userId, bookId);
        return userBookMapper.toResponse(userBook);
    }

    //Получить книги пользователя по статусу
    @Override
    public List<UserBookResponse> getUserBooksByStatus(Long userId, ReadingStatus status) {
        User user = userService.getUserEntityById(userId);

        return userBookRepository.findByUserAndStatus(user, status)
                .stream()
                .map(userBookMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Удалить книгу из библиотеки пользователя
    @Override
    @Transactional
    public void removeBookFromUser(Long userId, Long bookId) {
        UserBook userBook = getUserBookEntity(userId, bookId);
        userBookRepository.delete(userBook);

    }

    //Посчитать количество книг пользователя по статусу
    @Override
    public long countUserBooksByStatus(Long userId, ReadingStatus status) {
        User user = userService.getUserEntityById(userId);
        return userBookRepository.countByUserAndStatus(user, status);
    }

    // Приватный вспомогательный метод
    private UserBook getUserBookEntity(Long userId, Long bookId) {
        User user = userService.getUserEntityById(userId);
        return userBookRepository.findByUserAndBookId(user, bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга не найдена в библиотеке пользователя"));
    }
}
