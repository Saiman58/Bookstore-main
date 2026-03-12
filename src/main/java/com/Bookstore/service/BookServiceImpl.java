package com.Bookstore.service;

import com.Bookstore.dto.mapper.BookMapper;
import com.Bookstore.dto.request.BookCreateRequest;
import com.Bookstore.dto.response.BookResponse;
import com.Bookstore.exception.BookNotFoundException;
import com.Bookstore.model.entity.Book;
import com.Bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    // Сохранить книгу (если есть по ISBN - вернуть существующую)
    @Override
    @Transactional
    public BookResponse saveBook(BookCreateRequest request) {
        // Преобразуем Request в Entity
        Book book = bookMapper.toEntity(request);

        // Проверяем по ISBN - если книга уже есть, возвращаем её
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            return bookRepository.findByIsbn(book.getIsbn())
                    .map(bookMapper::toResponse)
                    .orElseGet(() -> {
                        Book savedBook = bookRepository.save(book);
                        return bookMapper.toResponse(savedBook);
                    });
        }

        // Если нет ISBN - просто сохраняем
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }


        // Найти книгу по ID
        @Override
        public BookResponse getBookById (Long id){
            Book book = getBookEntityById(id);
            return bookMapper.toResponse(book);
        }

        // Поиск книг по названию
        @Override
        public List<BookResponse> searchBooksByTitle(String title) {
            if (title == null || title.trim().isEmpty()) {
                return List.of();
            }

            return bookRepository.findByTitleContainingIgnoreCase(title.trim())
                    .stream()
                    .map(bookMapper::toResponse)
                    .collect(Collectors.toList());
        }

        // Поиск книг по автору
        @Override
        public List<BookResponse> searchBooksByAuthor(String author) {
            if (author == null || author.trim().isEmpty()) {
                return List.of();
            }

            return bookRepository.findByAuthorContainingIgnoreCase(author.trim())
                    .stream()
                    .map(bookMapper::toResponse)
                    .collect(Collectors.toList());
        }

        // Найти книгу по ISBN
        @Override
        public BookResponse getBookByIsbn(String isbn) {
            Book book = bookRepository.findByIsbn(isbn)
                    .orElseThrow(() -> new BookNotFoundException("Книга с ISBN " + isbn + " не найдена"));
            return bookMapper.toResponse(book);
        }

        // Получить все книги
        @Override
        public List<BookResponse> getAllBooks() {
            return bookRepository.findAll()
                    .stream()
                    .map(bookMapper::toResponse)
                    .collect(Collectors.toList());
        }

        // Удалить книгу
        @Override
        @Transactional
        public void deleteBook(Long id) {
            if (!bookRepository.existsById(id)) {
                throw new BookNotFoundException("Книга с id " + id + " не найдена");
            }
            bookRepository.deleteById(id);
        }

        @Override
        public Book getBookEntityById(Long id) {
            return bookRepository.findById(id)
                    .orElseThrow(() -> new BookNotFoundException("Книга с id " + id + " не найдена"));
        }
    }
