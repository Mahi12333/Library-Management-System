package org.librarymanagementsystem.payload.request.book;


import lombok.Builder;
import lombok.Getter;
import org.librarymanagementsystem.emun.BookStatus;

import java.time.LocalDateTime;


@Getter
@Builder
public class BookDTO {
    private String title;
    private String author;
    private String isbn;
    private BookStatus status;
    private Integer bookCount;
    private String description;
    private String category;

}
