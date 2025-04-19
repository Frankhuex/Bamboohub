package com.huex.bamboohub.converter;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookConverter {

    public BookDTO toDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getCreateTime(),
                book.getTitle(),
                book.getStartPara().getId(),
                book.getEndPara().getId(),
                book.getScope()
        );
    }

    public BookDTOWithRole toDTOWithRole(Role role) {
        return new BookDTOWithRole(
                role.getBook().getId(),
                role.getBook().getCreateTime(),
                role.getBook().getTitle(),
                role.getBook().getStartPara().getId(),
                role.getBook().getEndPara().getId(),
                role.getBook().getScope(),
                role.getRoleType()
        );
    }

    public BookDTOWithRole toDTOWithRole(Book book, Role.RoleType roleType) {
        return new BookDTOWithRole(
                book.getId(),
                book.getCreateTime(),
                book.getTitle(),
                book.getStartPara().getId(),
                book.getEndPara().getId(),
                book.getScope(),
                roleType
        );
    }


    public List<BookDTO> toDTOs(List<Book> books) {
        return books.stream()
               .map(this::toDTO)
               .collect(Collectors.toList());
    }

    public Book toDAO(BookReq bookReq) {
//        Book book = new Book();
//        book.setTitle(bookReq.getTitle());
//        book.setScope(bookReq.getScope());
//        return book;

        return new Book(
                bookReq.getTitle(),
                bookReq.getScope()
        );
    }

}
