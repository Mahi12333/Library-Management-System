package org.librarymanagementsystem.payload.request.borrowbook;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class BorrowedBookDTO {
    private Long bookId;
}
