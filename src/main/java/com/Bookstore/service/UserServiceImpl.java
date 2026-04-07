package com.Bookstore.service;

import com.Bookstore.dto.mapper.UserMapper;
import com.Bookstore.dto.request.UserCreateRequest;
import com.Bookstore.dto.request.UserUpdateRequest;
import com.Bookstore.dto.response.UserResponse;
import com.Bookstore.exception.UserNotFoundException;
import com.Bookstore.model.entity.User;
import com.Bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    //Регистрация нового пользователя
    @Override
    @Transactional
    public UserResponse registerUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username" + request.getUsername() + "' уже занят");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email '" + request.getEmail() + "' уже зарегистрирован");
        }
        // Маппинг Request -> Entity
        User user = userMapper.toEntity(request);
        // Хэшируем пароль в Entity, а не в Request!
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        // Возвращаем Response
        return userMapper.toResponse(savedUser);
    }

    // Поиск пользователя по ID
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
        return userMapper.toResponse(user);

    }

    // Поиск пользователя по username
    @Override
    public UserResponse getUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь '" + username + "' не найден"));
        return userMapper.toResponse(user);
    }

    // Получить всех пользователей
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    //Обновить данные пользователя
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {  // Принимаем Request, а не Entity!
        // Находим существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        // Обновляем поля, если они переданы
        if (request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }

        // Проверяем email, если он передан
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            // Проверяем, что новый email не занят другим пользователем
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(user -> {
                        if (!user.getId().equals(id)) {
                            throw new RuntimeException("Email '" + request.getEmail() + "' уже используется");
                        }
                    });
            existingUser.setEmail(request.getEmail());
        }

        // Если меняется пароль - хэшируем
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Сохраняем и возвращаем Response
        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с id \" + id + \" не найден");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Пользователь с id " + id + " не найден"));
    }
}
