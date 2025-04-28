package org.librarymanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.payload.request.borrowbook.BorrowedBookDTO;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowBookResponse;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowedBookReturnDTO;
import org.librarymanagementsystem.services.BookBorrowService;
import org.librarymanagementsystem.utils.constants.ConstantValue;
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

    @Operation(summary = "stock of Book Borrow Management ", description = "This ")
    @GetMapping("/get-all-borrowings")
    public ResponseEntity<BorrowBookResponse> getAllBorrowings(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNumber", defaultValue = ConstantValue.PAGE_NUMBER_BORROW_BOOK, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = ConstantValue.PAGE_SIZE_BORROW_BOOK, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = ConstantValue.SORT_USERS_BY_BORROW_BOOK, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = ConstantValue.SORT_DIR_BORROW_BOOK, required = false) String sortOrder
    ){
        BorrowBookResponse response =  bookBorrowService.getAllBorrowings(pageNumber, pageSize, sortBy, sortOrder, keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all-borrowings-of-a-member")
    public ResponseEntity<BorrowBookResponse> getAllBorrowingsOfAMember(
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNumber", defaultValue = ConstantValue.PAGE_NUMBER_BORROW_BOOK, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = ConstantValue.PAGE_SIZE_BORROW_BOOK, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = ConstantValue.SORT_USERS_BY_BORROW_BOOK, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = ConstantValue.SORT_DIR_BORROW_BOOK, required = false) String sortOrder
    ){
         BorrowBookResponse response = bookBorrowService.getAllBorrowingsOfAMember(userId, keyword, pageNumber, pageSize, sortBy, sortOrder);
         return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
