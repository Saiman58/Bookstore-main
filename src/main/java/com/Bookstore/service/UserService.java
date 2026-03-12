package com.Bookstore.service;


import com.Bookstore.dto.request.UserCreateRequest;
import com.Bookstore.dto.request.UserUpdateRequest;
import com.Bookstore.dto.response.UserResponse;
import com.Bookstore.model.entity.User;

import java.util.List;

public interface UserService {

    //Регистрация нового пользователя
    UserResponse registerUser(UserCreateRequest request);

    // Поиск пользователя по ID
    UserResponse getUserById(Long id);

    // Поиск пользователя по username
    UserResponse  getUserByUsername(String username);

    // Получить всех пользователей
    List<UserResponse> getAllUsers();

    //Обновить данные пользователя
    UserResponse updateUser(Long id, UserUpdateRequest request);

    // Удалить пользователя
    void deleteUser(Long id);

    // Для внутренней работы (полные данные)
    User getUserEntityById(Long id);

}
