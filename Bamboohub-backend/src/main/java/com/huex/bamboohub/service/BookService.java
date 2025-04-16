package com.huex.bamboohub.service;
import com.huex.bamboohub.dao.Book;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface BookService {
    BookDTO addNewBook(String token, BookReq bookReq);
    BookDTO getBookById(String token, Long id);

    List<BookDTO> getMyBooks(String token);
    List<BookDTO> getBooksByScope(String token, Book.Scope scope);

    List<BookDTO> searchBooksByTitle(String title);

    boolean deleteBookById(String token, Long id);
    BookDTO updateBookById(String token, Long id, BookUpdateReq bookUpdReq);
    
}
