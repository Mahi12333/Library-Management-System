package org.librarymanagementsystem.services;


import org.librarymanagementsystem.payload.request.book.BorrowedBookDTO;
import org.librarymanagementsystem.payload.response.book.BorrowedBookReturnDTO;

public interface BookBorrowService {
    BorrowedBookReturnDTO createBorrowBook(BorrowedBookDTO request);
    BorrowedBookReturnDTO createReturnBook(Long bookId);
    BorrowedBookDTO stock(Long bookId);
}
