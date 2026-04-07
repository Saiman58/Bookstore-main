package com.Bookstore.dto.mapper;

import com.Bookstore.dto.response.UserBookResponse;
import com.Bookstore.model.entity.UserBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBookMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserBookResponse toResponse(UserBook userBook) {
        return UserBookResponse.builder()
                .id(userBook.getId())
                .user(userMapper.toResponse(userBook.getUser()))
                .book(bookMapper.toResponse(userBook.getBook()))
                .status(userBook.getStatus())
                .startedAt(userBook.getStartedAt())
                .finishedAt(userBook.getFinishedAt())
                .userRating(userBook.getUserRating())
                .userReview(userBook.getUserReview())
                .addedAt(userBook.getAddedAt())
                .build();
    }
}
