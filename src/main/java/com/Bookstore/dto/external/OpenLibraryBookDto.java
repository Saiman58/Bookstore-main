package com.Bookstore.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDto {
    private String title;
    private List<String> author_name;
    private List<String> isbn;
    private String first_publish_year;
    private String publisher;
    private Integer number_of_pages_median;
    private String key;
    private Integer cover_i;

    public String getCoverUrl() {
        if (cover_i != null) {
            return "https://covers.openlibrary.org/b/id/" + cover_i + "-L.jpg";
        }
        return null;
    }
}
