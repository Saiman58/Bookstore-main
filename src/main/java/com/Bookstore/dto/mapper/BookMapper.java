package com.Bookstore.dto.mapper;

import com.Bookstore.dto.request.BookCreateRequest;
import com.Bookstore.dto.response.BookResponse;
import com.Bookstore.model.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Преобразование Entity -> Response
    public BookResponse toResponse(Book book) {
        if (book == null) {
            return null;
        }
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .pageCount(book.getPageCount())
                .publishedDate(book.getPublishedDate())
                .publisher(book.getPublisher())
                .coverImageUrl(book.getCoverImageUrl())
                .build();
    }

    //Преобразование Request -> Entity
    public Book toEntity(BookCreateRequest request) {
        if (request == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setIsbn(request.getIsbn());
        book.setPageCount(request.getPageCount());
        book.setPublisher(request.getPublisher());
        // publishedDate и coverImageUrl обычно приходят из внешнего API
        return book;
    }
}
