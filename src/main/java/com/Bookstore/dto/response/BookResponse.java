package com.Bookstore.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String isbn;
    private Integer pageCount;
    private LocalDate publishedDate;
    private String publisher;
    private String coverImageUrl;
}
