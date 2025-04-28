package org.librarymanagementsystem.mapstruct;

import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.model.Borrowed_book_records;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.payload.response.book.BookResponseDTO;
import org.librarymanagementsystem.payload.response.BorrowBooksDTO;
import org.librarymanagementsystem.security.response.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface BorrowBookMapper {
    BorrowBookMapper INSTANCE = Mappers.getMapper(BorrowBookMapper.class);

    default List<BorrowBooksDTO> getAllBorrowBookMP(List<Borrowed_book_records> records) {
        return records.stream().map(record -> {
            BorrowBooksDTO dto = new BorrowBooksDTO();
            dto.setId(record.getId());
            dto.setBorrowedAt(record.getBorrowedAt());
            dto.setDueDate(record.getDueDate());

            // Map User -> UserDTO
            User user = record.getUser();
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setEmail(user.getEmail());
                dto.setUser(userDTO);
            }

            // Map Book -> BookResponseDTO
            Book book = record.getBook();
            if (book != null) {
                BookResponseDTO bookResponseDTO = new BookResponseDTO();
                bookResponseDTO.setTitle(book.getTitle());
                bookResponseDTO.setAuthor(book.getAuthor());
                bookResponseDTO.setIsbn(book.getIsbn());
                bookResponseDTO.setStatus(book.getStatus()); // if you have status field (like AVAILABLE, BORROWED etc)
                dto.setBookResponseDTO(bookResponseDTO);
            }

            return dto;
        }).collect(Collectors.toList());
    }

   default List<BorrowBooksDTO> getAllUsersSpecificBorrowBookMP(List<Borrowed_book_records> records){
        return records.stream().map(record -> {
            BorrowBooksDTO dto = new BorrowBooksDTO();
            dto.setId(record.getId());
            dto.setBorrowedAt(record.getBorrowedAt());
            dto.setDueDate(record.getDueDate());

            // Map User -> UserDTO
            User user = record.getUser();
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setEmail(user.getEmail());
                dto.setUser(userDTO);
            }

            // Map Book -> BookResponseDTO
            Book book = record.getBook();
            if (book != null) {
                BookResponseDTO bookResponseDTO = new BookResponseDTO();
                bookResponseDTO.setTitle(book.getTitle());
                bookResponseDTO.setAuthor(book.getAuthor());
                bookResponseDTO.setIsbn(book.getIsbn());
                bookResponseDTO.setStatus(book.getStatus()); // if you have status field (like AVAILABLE, BORROWED etc)
                dto.setBookResponseDTO(bookResponseDTO);
            }

            return dto;
        }).collect(Collectors.toList());
    };
}
