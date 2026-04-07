package com.Bookstore.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class BookCreateRequest {

    @NotBlank(message = "Название книги обязательно")
    @Size(min = 1, max = 255, message = "Название должно быть от 1 до 255 символов")
    private String title;

    @NotBlank(message = "Автор обязателен")
    @Size(min = 1, max = 255, message = "Имя автора должно быть от 1 до 255 символов")
    private String author;

    @Size(max = 2000, message = "Описание не может быть длиннее 2000 символов")
    private String description;

    private String isbn;

    private Integer pageCount;

    private String publisher;

    private String coverImageUrl;
}
