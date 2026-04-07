package com.Bookstore.service;

import com.Bookstore.dto.request.BookCreateRequest;
import com.Bookstore.dto.response.BookResponse;
import com.Bookstore.model.entity.Book;

import java.util.List;

public interface BookService {

    // Сохранить книгу (если есть по ISBN - вернуть существующую)
    BookResponse saveBook(BookCreateRequest request);

    // Найти книгу по ID
    BookResponse getBookById(Long id);

    // Поиск книг по названию
    List<BookResponse> searchBooksByTitle(String title);

    // Поиск книг по автору
    List<BookResponse> searchBooksByAuthor(String author);

    // Найти книгу по ISBN
    BookResponse getBookByIsbn(String isbn);

    // Получить все книги
    List<BookResponse> getAllBooks();

    // Удалить книгу
    void deleteBook(Long id);

    // Внутренний метод (для других сервисов)
    Book getBookEntityById(Long id);
}
