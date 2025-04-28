package org.librarymanagementsystem.payload.response.borrowbook;

import lombok.Builder;
import lombok.Getter;

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
