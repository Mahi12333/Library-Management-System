package org.librarymanagementsystem.payload.response.book;

import lombok.Getter;
import lombok.Setter;
import org.librarymanagementsystem.emun.BookStatus;

@Getter
@Setter
public class BookResponseDTO {
    private String title;
    private String author;
    private String isbn;
    private BookStatus status;
    private String description;
    private String category;
}
