package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.TransactionType;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.mapstruct.BorrowBookMapper;
import org.librarymanagementsystem.model.*;
import org.librarymanagementsystem.payload.request.borrowbook.BorrowedBookDTO;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowBookResponse;
import org.librarymanagementsystem.payload.response.BorrowBooksDTO;
import org.librarymanagementsystem.payload.response.borrowbook.BorrowedBookReturnDTO;
import org.librarymanagementsystem.repository.BookBorrowRepository;
import org.librarymanagementsystem.repository.BookRepository;
import org.librarymanagementsystem.repository.FinesRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.services.BookBorrowService;
import org.librarymanagementsystem.services.NotificationService;
import org.librarymanagementsystem.utils.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookBorrowImp implements BookBorrowService {
    private final BookBorrowRepository bookBorrowRepository;
    private final BookRepository bookRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final FinesRepository finesRepository;
    private final NotificationService notificationService;
    private final BorrowBookMapper borrowBookMapper;


    @Transactional
    @Override
    public BorrowedBookReturnDTO createBorrowBook(BorrowedBookDTO request) {
        Optional<Book> bookDTO = bookRepository.findById(request.getBookId());
        if (bookDTO.isEmpty()) {
            throw new APIException("Book not found");
        }
        Long userId = authUtil.loggedInUserId();
        if (userId == null) {
            throw new APIException("User not found");
        }
        Book bookEntity = bookDTO.get();
        if (bookEntity.getBookCount() <= 0) {
            throw new APIException("Not enough copies available");
        }
        // Reduce the book count
        updateBookCopies(bookEntity, TransactionType.BORROW, 1);

        // Create Borrowed Book Record
        Borrowed_book_records borrowedBook = new Borrowed_book_records();
        borrowedBook.setUser(userRepository.findById(userId).orElseThrow(() -> new APIException("User not found")));
        borrowedBook.setBook(bookEntity);
        borrowedBook.setBorrowedAt(new Timestamp(System.currentTimeMillis()));
        borrowedBook.setDueDate(Date.valueOf(LocalDate.now().plusDays(14)));

        // Save Borrowed Book Record
        Borrowed_book_records recordWithRelations = bookBorrowRepository.save(borrowedBook);
        Borrowed_book_records savedRecord  = bookBorrowRepository
                .findByIdWithBookAndUser(recordWithRelations.getId())
                .orElseThrow(() -> new APIException("Borrowed record not found"));
        notificationService.borrowBookNotification(savedRecord); // Null Book problem
        notificationService.reminderNotification(savedRecord); // send this notification two days before the due date // Null Book problem
        // Return DTO
        return BorrowedBookReturnDTO.builder()
                .id(savedRecord.getId())
                .bookId(savedRecord.getBook().getId())
                .borrowedAt(savedRecord.getBorrowedAt())
                .dueDate(savedRecord.getDueDate())
                .build();
    }

    @Override
    public BorrowedBookReturnDTO createReturnBook(Long bookId) {
        Optional<Borrowed_book_records> optionalBorrowedBook = bookBorrowRepository.findById(bookId);
        if (optionalBorrowedBook.isEmpty()) {
            throw new APIException("Book not found");
        }
        Borrowed_book_records borrowedBook = optionalBorrowedBook.get();
        log.info("borrowedBook--{}",borrowedBook.getReturnAt());
        Optional<User> userDB = userRepository.findById(borrowedBook.getUser().getId());
        if(userDB.isEmpty()){
            throw new APIException("Member not found");
        }
        if (borrowedBook.getReturnAt() != null) {
            throw new APIException("Book has already been returned");
        }
        if(borrowedBook.getDueDate().before(new java.util.Date())) {
            log.info("getDueDate--{}",borrowedBook.getDueDate());
            if (borrowedBook.getFine() == null) {
                Fines fine = imposeFine(borrowedBook);
                log.info("fines return-- {}", fine);
                borrowedBook.setFine(fine);
                bookBorrowRepository.save(borrowedBook);
                notificationService.fineImposedNotification(borrowedBook);
                throw new APIException("Due date passed. Fine imposed, pay fine first to return the book");
            } else if (!borrowedBook.getFine().isPaid()) {
                notificationService.fineImposedNotification(borrowedBook);
                throw new APIException("Outstanding fine, please pay before returning the book");
            }
        }

        borrowedBook.setReturnAt(new java.util.Date());
        Optional<Book> bookDto = bookRepository.findById(borrowedBook.getBook().getId());
        Book bookEntity = bookDto.get();
        updateBookCopies(bookEntity, TransactionType.RETURN, 1);
        notificationService.bookReturnedNotification(borrowedBook);
        bookBorrowRepository.save(borrowedBook);
        // Return DTO
        return BorrowedBookReturnDTO.builder()
                .id(borrowedBook.getId())
                .bookId(borrowedBook.getBook().getId())
                .borrowedAt(borrowedBook.getBorrowedAt())
                .returnedAt(borrowedBook.getReturnAt())
                .dueDate(borrowedBook.getDueDate())
                .build();
    }

    @Override
    public BorrowedBookDTO stock(Long bookId) {
        return null;
    }

    @Override
    public BorrowBookResponse getAllBorrowings(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        // TODO LIST
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Borrowed_book_records> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.join("book").get("title")), "%" + keyword.toLowerCase() + "%"));
        }

        Page<Borrowed_book_records> borrowedBookPage = bookBorrowRepository.findAll(spec, pageDetails);
        List<Borrowed_book_records> borrowedBooks = borrowedBookPage.getContent();

        if (borrowedBooks.isEmpty()) {
            throw new APIException("No Borrowed Books Exist Now!");
        }

        List<BorrowBooksDTO> booksDTOS = borrowBookMapper.getAllBorrowBookMP(borrowedBooks);

        BorrowBookResponse bookResponse = new BorrowBookResponse();
        bookResponse.setContent(booksDTOS);
        bookResponse.setPageNumber(borrowedBookPage.getNumber());
        bookResponse.setLastPage(borrowedBookPage.isLast());
        bookResponse.setPageSize(borrowedBookPage.getSize());
        bookResponse.setTotalElements(borrowedBookPage.getTotalElements());
        bookResponse.setTotalPages(borrowedBookPage.getTotalPages());

        return bookResponse;
    }

    @Override
    public BorrowBookResponse getAllBorrowingsOfAMember(Long userId, String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // TODO LIST
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            throw new APIException("User not exists");
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Borrowed_book_records> borrowPage = bookBorrowRepository.findByUserIdAndKeyword(userId, keyword == null ? "" : keyword, pageable);
        List<Borrowed_book_records> borrowList = borrowPage.getContent();

        List<BorrowBooksDTO> booksDTOS = borrowBookMapper.getAllUsersSpecificBorrowBookMP(borrowList);
        BorrowBookResponse response = new BorrowBookResponse();

        if(booksDTOS.isEmpty()){
            response.setContent(0); // Corrected line
        }else {
         response.setContent(booksDTOS); // Corrected line
        }
        //response.setContent(booksDTOS); // Corrected line
        response.setPageNumber(borrowPage.getNumber());
        response.setPageSize(borrowPage.getSize());
        response.setTotalElements(borrowPage.getTotalElements());
        response.setTotalPages(borrowPage.getTotalPages());
        response.setLastPage(borrowPage.isLast());

        return response;
    }

    public void updateBookCopies(Book bookEntity, TransactionType operation, int numberOfCopies) {
        if (operation == TransactionType.RETURN) {
            bookEntity.setBookCount(bookEntity.getBookCount() + numberOfCopies);
        } else if (operation.equals(TransactionType.BORROW)) {
            if (bookEntity.getBookCount() >= numberOfCopies) {
                bookEntity.setBookCount(bookEntity.getBookCount() - numberOfCopies);
            } else {
                throw new APIException("Not enough copies available");
            }
        }
        // Save the updated book count
        bookRepository.save(bookEntity);
    }

    private Fines imposeFine(Borrowed_book_records borrowing) {
        Fines fine = new Fines();
        Long userId = authUtil.loggedInUserId();
        if (userId == null) {
            throw new APIException("User not found");
        }
        fine.setUser(userRepository.findById(userId).orElseThrow(() -> new APIException("User not found")));
        fine.setBook(borrowing.getBook());
        fine.setAmount(calculateFineAmount(borrowing));
        return finesRepository.save(fine);
    }

    private BigDecimal calculateFineAmount(Borrowed_book_records borrowing) {
        long overdueDays = ChronoUnit.DAYS.between(
                borrowing.getDueDate().toInstant(),
                new java.util.Date().toInstant());
        System.out.println("Over Due Days" + overdueDays);
        System.out.println("Fine Amount" + (overdueDays * 10));
        return BigDecimal.valueOf(overdueDays * 10); // 10 rupees per day fine
    }

    private Date calculateDueDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 15);
        return (Date) calendar.getTime();
    }


}
