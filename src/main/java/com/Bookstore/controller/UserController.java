package com.Bookstore.controller;

import com.Bookstore.dto.mapper.UserMapper;
import com.Bookstore.dto.request.UserCreateRequest;
import com.Bookstore.dto.request.UserUpdateRequest;
import com.Bookstore.dto.response.UserResponse;
import com.Bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    //POST /api/users/register - регистрация нового пользователя
    @PostMapping("register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserCreateRequest request) {  // @Valid для валидации
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    //GET /api/users/{id} - получить пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //GET /api/users/username/{username} - получить по username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    //GET /api/users - список всех пользователей
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //PUT /api/users/{id} - обновить пользователя
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    //DELETE /api/users/{id} - удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) { // НЕ УДАЛЯЕТ ПОЛЬЗОВАТЕЛЯ!
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
