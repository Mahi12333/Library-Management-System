package org.librarymanagementsystem.payload.request.book;

import lombok.Builder;
import lombok.Getter;
import org.librarymanagementsystem.model.Fines;

import java.util.Date;


@Getter
@Builder
public class BorrowedBookDTO {
    private Long bookId;
}
