package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.payload.request.book.BorrowedBookDTO;
import org.librarymanagementsystem.payload.response.book.BorrowedBookReturnDTO;
import org.librarymanagementsystem.services.BookBorrowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "BookBorrowControllerController", description = "Book Borrow Management")
@RestController
@RequestMapping("/v1/api/transitional")
@RequiredArgsConstructor
public class BookBorrowController {
    private final BookBorrowService bookBorrowService;

     @Operation(summary = "borrow of Book Borrow Management ", description = "This ")
     @PostMapping("/borrow")
     public ResponseEntity<?> BookBorrow(@Valid @RequestBody BorrowedBookDTO request){
         BorrowedBookReturnDTO createTransitional = bookBorrowService.createBorrowBook(request);
         return new ResponseEntity<>(createTransitional, HttpStatus.CREATED);
     }

    @Operation(summary = "return of Book Borrow Management ", description = "This ")
    @PostMapping("/return")
    public ResponseEntity<?> BookReturn(@Valid @RequestBody Long bookId){
        BorrowedBookReturnDTO createTransitional = bookBorrowService.createReturnBook(bookId);
        return new ResponseEntity<>(createTransitional, HttpStatus.CREATED);
    }

    @Operation(summary = "stock of Book Borrow Management ", description = "This ")
    @PostMapping("/stock")
    public ResponseEntity<?> BookStock(@Valid @RequestBody Long bookId){
        BorrowedBookDTO createTransitional = bookBorrowService.stock(bookId);
        return new ResponseEntity<>(createTransitional, HttpStatus.OK);
    }





}
