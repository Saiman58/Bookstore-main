package com.Bookstore.service;

import com.Bookstore.dto.external.OpenLibraryBookDto;
import com.Bookstore.dto.request.BookCreateRequest;
import com.Bookstore.dto.response.BookResponse;
import com.Bookstore.service.external.OpenLibraryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookImportService {

    private final OpenLibraryClient openLibraryClient;
    private final BookService bookService;

    /**
     * Импортировать книгу по ISBN
     */
    public BookResponse importBookByIsbn(String isbn) {
        log.info("Импорт книги по ISBN: {}", isbn);

        // Проверяем, есть ли уже такая книга в базе
        try {
            BookResponse existingBook = bookService.getBookByIsbn(isbn);
            log.info("Книга уже существует в базе: {}", existingBook.getTitle());
            return existingBook;
        } catch (Exception e) {
            log.info("Книга не найдена в базе, импортируем из Open Library");
        }

        // Ищем в Open Library
        OpenLibraryBookDto openLibraryBook = openLibraryClient.searchByIsbn(isbn);
        if (openLibraryBook == null) {
            throw new RuntimeException("Книга с ISBN " + isbn + " не найдена в Open Library");
        }

        // Конвертируем в запрос для сохранения
        BookCreateRequest request = convertToRequest(openLibraryBook);

        // Сохраняем книгу
        return bookService.saveBook(request);
    }

    /**
     * Поиск книг по названию через Open Library
     */
    public List<BookResponse> searchBooksByTitle(String title) {
        log.info("Поиск книг по названию: {}", title);

        List<OpenLibraryBookDto> externalBooks = openLibraryClient.searchByTitle(title);

        return externalBooks.stream()
                .map(this::convertToResponse)
                .limit(20)
                .collect(Collectors.toList());
    }

    /**
     * Поиск книг по автору через Open Library
     */
    public List<BookResponse> searchBooksByAuthor(String author) {
        log.info("Поиск книг по автору: {}", author);

        List<OpenLibraryBookDto> externalBooks = openLibraryClient.searchByAuthor(author);

        return externalBooks.stream()
                .map(this::convertToResponse)
                .limit(20)
                .collect(Collectors.toList());
    }

    private BookCreateRequest convertToRequest(OpenLibraryBookDto dto) {
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle(dto.getTitle());

        if (dto.getAuthor_name() != null && !dto.getAuthor_name().isEmpty()) {
            request.setAuthor(dto.getAuthor_name().get(0));
        } else {
            request.setAuthor("Неизвестный автор");
        }

        if (dto.getIsbn() != null && !dto.getIsbn().isEmpty()) {
            request.setIsbn(dto.getIsbn().get(0));
        }

        if (dto.getNumber_of_pages_median() != null) {
            request.setPageCount(dto.getNumber_of_pages_median());
        }

        if (dto.getPublisher() != null) {
            request.setPublisher(dto.getPublisher());
        }

        if (dto.getCoverUrl() != null) {
            request.setCoverImageUrl(dto.getCoverUrl());
        }

        return request;
    }

    private BookResponse convertToResponse(OpenLibraryBookDto dto) {
        String author = dto.getAuthor_name() != null && !dto.getAuthor_name().isEmpty()
                ? dto.getAuthor_name().get(0) : "Неизвестный автор";

        return BookResponse.builder()
                .title(dto.getTitle())
                .author(author)
                .isbn(dto.getIsbn() != null && !dto.getIsbn().isEmpty() ? dto.getIsbn().get(0) : null)
                .pageCount(dto.getNumber_of_pages_median())
                .publisher(dto.getPublisher())
                .coverImageUrl(dto.getCoverUrl())
                .build();
    }


}