package org.librarymanagementsystem.services;


import org.librarymanagementsystem.payload.request.borrowbook.BorrowedBookDTO;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowBookResponse;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowedBookReturnDTO;

public interface BookBorrowService {
    BorrowedBookReturnDTO createBorrowBook(BorrowedBookDTO request);
    BorrowedBookReturnDTO createReturnBook(Long bookId);
    BorrowedBookDTO stock(Long bookId);
    BorrowBookResponse getAllBorrowings(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);
    BorrowBookResponse getAllBorrowingsOfAMember(Long userId, String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
