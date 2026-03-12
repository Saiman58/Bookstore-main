package com.Bookstore.dto.response;

import com.Bookstore.model.enums.ReadingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserBookResponse {
    private Long id;
    private UserResponse user;      // переиспользуем!
    private BookResponse book;      // переиспользуем!
    private ReadingStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer userRating;
    private String userReview;
    private LocalDateTime addedAt;
}
