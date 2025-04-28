package org.librarymanagementsystem.payload.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class BookUpdateResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private Long BookCount;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
