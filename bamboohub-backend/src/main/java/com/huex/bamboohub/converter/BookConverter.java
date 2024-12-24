package com.huex.bamboohub.converter;

import com.huex.bamboohub.dao.Book;
import com.huex.bamboohub.dto.BookDTO;
import com.huex.bamboohub.request.BookRequest;

public interface BookConverter {
    public BookDTO toDTO(Book book);
    public Book toDAO(BookRequest bookReq);
}
