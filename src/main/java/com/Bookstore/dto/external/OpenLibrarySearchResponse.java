package com.Bookstore.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibrarySearchResponse {
    private Integer numFound;
    private List<OpenLibraryBookDto> docs;
}