package com.Bookstore.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Data
public class ReviewRequest {

    @Min(value = 1, message = "Оценка должна быть не меньше 1")
    @Max(value = 5, message = "Оценка должна быть не больше 5")
    private Integer rating;

    @Size(max = 1000, message = "Отзыв не может быть длиннее 1000 символов")
    private String review;
}
