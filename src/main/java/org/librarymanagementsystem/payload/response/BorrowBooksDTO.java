package org.librarymanagementsystem.payload.response;


import lombok.Getter;
import lombok.Setter;
import org.librarymanagementsystem.payload.response.book.BookResponseDTO;
import org.librarymanagementsystem.security.response.UserDTO;

import java.util.Date;

@Setter
@Getter
public class BorrowBooksDTO {
    private Long id;
    private Date borrowedAt;
    private Date dueDate;
    private UserDTO user;
    private BookResponseDTO bookResponseDTO;
}
