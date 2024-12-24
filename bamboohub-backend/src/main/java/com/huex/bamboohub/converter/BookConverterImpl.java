package com.huex.bamboohub.converter;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@Component
public class BookConverterImpl implements BookConverter {
    @Override
    public BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        if (book.getStartPara()!=null) {
            bookDTO.setStartParaId(book.getStartPara().getId());
        }
        return bookDTO;
    }

    @Override
    public Book toDAO(BookRequest bookReq) {
        Book book = new Book();
        book.setTitle(bookReq.getTitle());
        return book;
    }
}
