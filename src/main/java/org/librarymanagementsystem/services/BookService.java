package org.librarymanagementsystem.services;

import jakarta.validation.Valid;
import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.payload.request.book.BookDTO;
import org.librarymanagementsystem.payload.request.book.BookUpdateDTO;
import org.librarymanagementsystem.payload.response.book.BookResponse;
import org.librarymanagementsystem.payload.response.book.BookUpdateResponse;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO request);

    void deleteBook(Long id);

    Book editBook(Long id);

    BookUpdateResponse udpateBookDetails(BookUpdateDTO request);

    BookResponse getallBook(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);

    List<String> category();
}
