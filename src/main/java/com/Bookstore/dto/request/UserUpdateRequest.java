package com.Bookstore.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateRequest {

    @Email(message = "Некорректный формат email")
    private String email;

    @Size(min = 6, max = 100, message = "Пароль должен быть минимум 6 символов")
    private String password;

    private String firstName;
    private String lastName;
}
