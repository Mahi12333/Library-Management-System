package org.librarymanagementsystem.mapstruct;

import org.librarymanagementsystem.model.Book;
import org.librarymanagementsystem.payload.request.book.BookDTO;
import org.librarymanagementsystem.payload.request.book.BookUpdateDTO;
import org.librarymanagementsystem.payload.response.book.BookUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book toEntity(BookDTO dto);
    BookDTO toDto(Book book);
    Book toEdit(Book bookDB);
    List<Book> getAllUsersMP(List<Book> books);

    void updateBookFromDto(BookUpdateDTO dto, @MappingTarget Book book);

    BookUpdateResponse updateBooktoDto(Book book);

}
