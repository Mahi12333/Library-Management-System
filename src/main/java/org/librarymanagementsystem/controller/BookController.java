package org.librarymanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.payload.request.BookDTO;
import org.librarymanagementsystem.payload.request.BookUpdateDTO;
import org.librarymanagementsystem.payload.response.book.BookResponse;
import org.librarymanagementsystem.payload.response.BookUpdateResponse;
import org.librarymanagementsystem.services.BookService;
import org.librarymanagementsystem.utils.constants.ConstantValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "BookController", description = "Book Management")
@RestController
@RequestMapping("/v1/api/book")
@RequiredArgsConstructor
public class BookController {
    // TODO: Implement book management endpoints
    private final BookService bookService;

    @Operation(summary = "create of Book  Management ", description = "This ")
    @PostMapping("/create")
    public ResponseEntity<?> BookCreate(@Valid @RequestBody BookDTO request){
        BookDTO createResponse = bookService.createBook(request);
        return new ResponseEntity<>(createResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "delete of Book  Management ", description = "This ")
    @DeleteMapping("/delete")
    public ResponseEntity<?> DeleteBook(@Valid @RequestBody Long id){
         bookService.deleteBook(id);
        return ResponseEntity.ok("Book successfully Delete!");
    }

    @Operation(summary = "edit of Book  Management ", description = "This ")
    @GetMapping("/edit")
    public ResponseEntity<?> BookEdit(@Valid @RequestParam Long id){
        Book response = bookService.editBook(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Book", description = "Partially update a book using PATCH.")
    @PatchMapping("/update")
    public ResponseEntity<?> BookUpdate(@Valid @RequestBody BookUpdateDTO request){
        BookUpdateResponse bookUpdateResponse = bookService.udpateBookDetails(request);
        return new ResponseEntity<>(bookUpdateResponse, HttpStatus.OK);
    }

    @Operation(summary = "all-book of Book  Management ", description = "This ")
    @GetMapping("/all-book")
    public ResponseEntity<BookResponse> AllBook(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNumber", defaultValue = ConstantValue.PAGE_NUMBER_BOOK, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = ConstantValue.PAGE_SIZE_BOOK, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = ConstantValue.SORT_USERS_BY_BOOK, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = ConstantValue.SORT_DIR_BOOK, required = false) String sortOrder
    ){
        BookResponse bookResponse = bookService.getallBook(pageNumber, pageSize, sortBy, sortOrder, keyword);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    @Operation(summary = "category of Book  Management ", description = "This ")
    @GetMapping("/category")
    public ResponseEntity<?> getCategory(){
        List<String> categoryResponse = bookService.category();
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }



}
