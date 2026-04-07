# Bookstore — Frontend

Простой статичный фронтенд для Spring Boot API.

## Запуск

1. Запустите бэкенд: `./mvnw spring-boot:run` (порт 8081)
2. Откройте `frontend/index.html` в браузере **через локальный сервер** (не двойным кликом — нужен HTTP для fetch-запросов).

### Быстрый вариант — VS Code Live Server
Установите расширение "Live Server", кликните правой кнопкой на `index.html` → "Open with Live Server".

### Через Python
```bash
cd frontend
python3 -m http.server 3000
# Откройте http://localhost:3000
```

## CORS

В бэкенде нужно разрешить запросы с localhost. Добавьте в `SecurityConfig.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
    config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

## Структура

```
frontend/
  index.html   — разметка (три страницы + модальные окна)
  style.css    — стили (CSS Variables, адаптивность)
  app.js       — логика (fetch к API, рендер, события)
```

## Функциональность

| Раздел      | Возможности |
|-------------|-------------|
| Каталог     | Поиск по названию/автору, просмотр карточки, добавление книги, удаление |
| Библиотека  | Список книг пользователя, фильтр по статусу, смена статуса, отзыв+рейтинг, удаление |
| Статистика  | Счётчики по статусам + столбчатая диаграмма |
| Аккаунт     | Регистрация, вход по username |
