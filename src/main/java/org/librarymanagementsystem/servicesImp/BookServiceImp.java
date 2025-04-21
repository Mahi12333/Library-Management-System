package org.librarymanagementsystem.servicesImp;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.emun.BookStatus;
import org.librarymanagementsystem.exception.APIException;
import org.librarymanagementsystem.exception.ResourceNotFoundException;
import org.librarymanagementsystem.mapstruct.BookMapper;
import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.payload.request.book.BookDTO;
import org.librarymanagementsystem.payload.request.book.BookUpdateDTO;
import org.librarymanagementsystem.payload.response.book.BookResponse;
import org.librarymanagementsystem.payload.response.book.BookUpdateResponse;
import org.librarymanagementsystem.repository.BookRepository;
import org.librarymanagementsystem.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImp implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;


    @Override
    public BookDTO createBook(BookDTO request) {
        Book bookEntity = bookMapper.toEntity(request);
        bookEntity.setStatus(BookStatus.AVAILABLE);
        Book savedBook = bookRepository.save(bookEntity); //! remove saveAndFlush
        return bookMapper.toDto(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book bookDB = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "Not Found", id));
        bookRepository.delete(bookDB);
    }

    @Override
    public Book editBook(Long id) {
        Book bookDB = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "Not Found", id));
        return bookMapper.toEdit(bookDB);

    }

    @Transactional
    @Override
    public BookUpdateResponse udpateBookDetails(BookUpdateDTO request) {
        Book existingBook = bookRepository.findById(request.getId()).orElseThrow(() -> new ResourceNotFoundException("Book", "Not Found"));
        if (isBookChanged(request, existingBook)) {
            bookMapper.updateBookFromDto(request, existingBook);
            existingBook.setUpdatedAt(LocalDateTime.now());
            Book updatedBook = bookRepository.save(existingBook);
            return bookMapper.updateBooktoDto(updatedBook);
        } else {
            log.info("No changes detected. Skipping update.");
            throw new APIException("No changes detected. Skipping update.");
        }
    }

    @Override
    public BookResponse getallBook(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Book> spec = Specification.where(null);
        //!This is a lambda function that defines the filtering logic.
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
        }
        Page<Book> usersPage = bookRepository.findAll(spec, pageDetails);
        List<Book> users = usersPage.getContent();
        if (users.isEmpty()) {
            throw new APIException("No User Exist Now!");
        }

        List<Book> booksDTOS = bookMapper.getAllUsersMP(users);

        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(booksDTOS);
        bookResponse.setPageNumber(usersPage.getNumber());
        bookResponse.setLastPage(usersPage.isLast());
        bookResponse.setPageSize(usersPage.getSize());
        bookResponse.setTotalElements(usersPage.getTotalElements());
        bookResponse.setTotalPages(usersPage.getTotalPages());

        return bookResponse;
    }

    @Override
    public List<String> category() {
       return bookRepository.findAll().stream().map(Book::getCategory).distinct().collect(Collectors.toList());
    }


    public boolean isBookChanged(BookUpdateDTO dto, Book book) {
        if (!dto.getTitle().equals(book.getTitle())) return true;
        if (!dto.getAuthor().equals(book.getAuthor())) return true;
        if (!dto.getIsbn().equals(book.getIsbn())) return true;
        if (!dto.getStatus().equals(book.getStatus())) return true;
        if (!dto.getBookCount().equals(book.getBookCount())) return true;
        //if (dto.getDescription() != null && !dto.getDescription().equals(book.getDescription())) return true;
        String dtoDesc = dto.getDescription() == null ? "" : dto.getDescription().trim().toLowerCase();
        String entityDesc = book.getDescription() == null ? "" : book.getDescription().trim().toLowerCase();
        if (!dtoDesc.equals(entityDesc)) return true;
        if (!dto.getCategory().equals(book.getCategory())) return true;

        // If nothing changed
        return false;
    }




}
