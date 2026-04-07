package com.Bookstore.dto.request;

import lombok.Data;

@Data
public class BookSearchRequest {
    private String title;
    private String author;
    private String isbn;

    // Можно добавить пагинацию позже
    private Integer page = 0;
    private Integer size = 20;
}
