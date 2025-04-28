package org.librarymanagementsystem.payload.response.borrowbook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.librarymanagementsystem.payload.response.BorrowBooksDTO;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BorrowBookResponse {
   // List<BorrowBooksDTO> content;
    private  Object content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
