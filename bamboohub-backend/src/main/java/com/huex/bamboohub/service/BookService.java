package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface BookService {
    Long addNewBook(BookRequest bookDTO);
    BookDTO getBookById(Long id);
    List<BookDTO> getAllBooks();
    void deleteBookById(Long id);
    BookDTO updateBookById(Long id, BookUpdateRequest bookUpdReq);
    List<Long> getAllBookIds();
}
