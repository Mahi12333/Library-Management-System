package org.librarymanagementsystem.payload.response.book;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
public class BorrowedBookReturnDTO {
    private Long id;
    private Long bookId;
    private Date borrowedAt;
    private Date dueDate;
    private Date returnedAt;
//    private Fines fines;
}
