package com.Bookstore.service.external;

import com.Bookstore.dto.external.OpenLibraryBookDto;
import com.Bookstore.dto.external.OpenLibrarySearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenLibraryClient {

    private final RestTemplate restTemplate;

    // Конструктор
    public OpenLibraryClient() {
        this.restTemplate = new RestTemplate();
    }

    // Поиск по названию
    public List<OpenLibraryBookDto> searchByTitle(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
           String url = "https://openlibrary.org/search.json?title=" + encodedTitle + "&limit=50";

            log.info("Запрос к Open Library: {}", url);

            OpenLibrarySearchResponse response = restTemplate.getForObject(url, OpenLibrarySearchResponse.class);

            if (response != null && response.getDocs() != null) {
                log.info("Найдено {} книг по названию: {}", response.getDocs().size(), title);
                return response.getDocs();
            }
        } catch (Exception e) {
            log.error("Ошибка при поиске по названию: {}", title, e);
        }
        return new ArrayList<>();
    }

    // Поиск по автору
    public List<OpenLibraryBookDto> searchByAuthor(String author) {
        try {
            String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8.toString());
            String url = "https://openlibrary.org/search.json?author=" + encodedAuthor + "&limit=10";

            log.info("Запрос к Open Library: {}", url);

            OpenLibrarySearchResponse response = restTemplate.getForObject(url, OpenLibrarySearchResponse.class);

            if (response != null && response.getDocs() != null) {
                log.info("Найдено {} книг по автору: {}", response.getDocs().size(), author);
                return response.getDocs();
            }
        } catch (Exception e) {
            log.error("Ошибка при поиске по автору: {}", author, e);
        }
        return new ArrayList<>();
    }

    // Поиск по ISBN
    public OpenLibraryBookDto searchByIsbn(String isbn) {
        try {
            String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

            log.info("Запрос к Open Library: {}", url);

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);

            if (response != null && response.containsKey("ISBN:" + isbn)) {
                log.info("Книга найдена по ISBN: {}", isbn);
                return parseBookFromResponse(response, isbn);
            }
        } catch (Exception e) {
            log.error("Ошибка при поиске по ISBN: {}", isbn, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private OpenLibraryBookDto parseBookFromResponse(java.util.Map<String, Object> response, String isbn) {
        try {
            java.util.Map<String, Object> bookData = (java.util.Map<String, Object>) response.get("ISBN:" + isbn);

            OpenLibraryBookDto book = new OpenLibraryBookDto();
            book.setTitle((String) bookData.get("title"));

            // Авторы
            List<java.util.Map<String, String>> authors = (List<java.util.Map<String, String>>) bookData.get("authors");
            if (authors != null && !authors.isEmpty()) {
                List<String> authorNames = new ArrayList<>();
                for (java.util.Map<String, String> author : authors) {
                    authorNames.add(author.get("name"));
                }
                book.setAuthor_name(authorNames);
            }

            // ISBN
            List<String> isbns = new ArrayList<>();
            isbns.add(isbn);
            book.setIsbn(isbns);

            // Количество страниц
            if (bookData.containsKey("number_of_pages")) {
                Object pages = bookData.get("number_of_pages");
                if (pages instanceof Integer) {
                    book.setNumber_of_pages_median((Integer) pages);
                }
            }

            // Издатель
            if (bookData.containsKey("publishers")) {
                List<java.util.Map<String, String>> publishers = (List<java.util.Map<String, String>>) bookData.get("publishers");
                if (publishers != null && !publishers.isEmpty()) {
                    book.setPublisher(publishers.get(0).get("name"));
                }
            }

            return book;
        } catch (Exception e) {
            log.error("Ошибка при парсинге ответа", e);
            return null;
        }
    }
}