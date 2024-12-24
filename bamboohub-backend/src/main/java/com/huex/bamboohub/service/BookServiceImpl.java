package com.huex.bamboohub.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.converter.*;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private ParagraphRepo paraRepo;

    @Autowired
    private BookConverterImpl bookConverter;

    @Override
    public Long addNewBook(BookRequest bookReq) throws IllegalArgumentException {
        List<Book> books = bookRepo.findByTitle(bookReq.getTitle());
        if (!CollectionUtils.isEmpty(books)) {
            throw new IllegalArgumentException("Book title "+bookReq.getTitle()+" already exists.");
        }    
        
        Book book=bookConverter.toDAO(bookReq);
        Book savedBook=bookRepo.save(book);

        Paragraph startPara=new Paragraph();
        startPara.setBook(savedBook);
        Paragraph savedPara=paraRepo.save(startPara);
        
        savedBook.setStartPara(savedPara);
        paraRepo.save(savedPara);
        bookRepo.save(savedBook);
        return savedBook.getId();
    }

    @Override
    public BookDTO getBookById(Long id) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        return bookConverter.toDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            bookDTOs.add(bookConverter.toDTO(book));
        }
        return bookDTOs;
    }
    
    @Override
    public void deleteBookById(Long id) throws IllegalArgumentException {
        bookRepo.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("Book with id "+id+" not found."));
        bookRepo.deleteById(id);
    }

    @Override
    public BookDTO updateBookById(Long id, BookUpdateRequest bookUpdReq) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        book.setTitle(bookUpdReq.getTitle());
        bookRepo.save(book);
        return bookConverter.toDTO(book);
    }
    
    @Override
    public List<Long> getAllBookIds() {
        List<Book> books = bookRepo.findAll();
        List<Long> bookIds = new ArrayList<>();
        for (Book book : books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }
}
