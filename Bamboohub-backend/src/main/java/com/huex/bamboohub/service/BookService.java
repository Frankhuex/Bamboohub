package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface BookService {
    Long addNewBook(String token, BookReq bookDTO);
    BookDTO getBookById(String token, Long id);
    List<Long> getAllPrivateBookIds(String token);
    List<Long> getAllPublicBookIds();
    List<BookDTO> getAllPrivateBooks(String token); 
    List<BookDTO> getAllPublicBooks();
    void deleteBookById(String token, Long id);
    BookDTO updateBookById(String token, Long id, BookUpdateReq bookUpdReq);
    
}
