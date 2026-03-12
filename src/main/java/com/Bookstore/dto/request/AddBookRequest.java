package com.Bookstore.dto.request;

import com.Bookstore.model.enums.ReadingStatus;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AddBookRequest {

    @NotNull(message = "ID книги обязателен")
    private Long bookId;

    private ReadingStatus status = ReadingStatus.WANT_TO_READ;
}
