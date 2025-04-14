package com.huex.bamboohub.converter;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@Component
public class BookConverter {

    public BookDTO toDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getCreateTime(),
                book.getTitle(),
                book.getStartPara().getId(),
                book.getScope()
        );
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
