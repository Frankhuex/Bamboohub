package com.huex.bamboohub.converter;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@Component
public class BookConverter {

    public BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        if (book.getStartPara()!=null) {
            bookDTO.setStartParaId(book.getStartPara().getId());
        }
        bookDTO.setIsPublic(book.getIsPublic());
        return bookDTO;
    }

    public Book toDAO(BookRequest bookReq) {
        Book book = new Book();
        book.setTitle(bookReq.getTitle());
        book.setIsPublic(bookReq.getIsPublic());
        return book;
    }
}
