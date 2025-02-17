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
import com.huex.bamboohub.util.*;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private ParagraphRepo paraRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BookConverter bookConverter;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleUtil roleUtil;

    @Override
    public Long addNewBook(String token, BookRequest bookReq) throws IllegalArgumentException {
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

        User owner=jwtUtil.parseUser(token);
        roleUtil.putRole(owner, savedBook, Role.RoleType.OWNER);

        return savedBook.getId();
    }

    @Override
    public BookDTO getBookById(String token, Long id) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!book.getIsPublic() && !roleUtil.canView(token,id)) { 
            throw new IllegalArgumentException("No permission to access book.");
        }
        return bookConverter.toDTO(book);
    }

    @Override
    public List<Long> getAllPrivateBookIds(String token) {
        List<Role> roles=roleRepo.findByUser(jwtUtil.parseUser(token));
        List<Long> bookIds = new ArrayList<>();
        for (Role role : roles) {
            bookIds.add(role.getBook().getId());
        }
        return bookIds;
    }

    @Override
    public List<Long> getAllPublicBookIds() {
        List<Book> books = bookRepo.findByIsPublic(true);
        List<Long> bookIds = new ArrayList<>();
        for (Book book : books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }

    @Override
    public List<BookDTO> getAllPrivateBooks(String token) {
        List<Role> roles=roleRepo.findByUser(jwtUtil.parseUser(token));
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Role role : roles) {
            bookDTOs.add(bookConverter.toDTO(role.getBook()));
        }
        return bookDTOs;
    }

    @Override
    public List<BookDTO> getAllPublicBooks() {
        List<Book> books = bookRepo.findByIsPublic(true);
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            bookDTOs.add(bookConverter.toDTO(book));
        }
        return bookDTOs;
    }
    
    @Override
    public void deleteBookById(String token, Long id) throws IllegalArgumentException {
        Book book=bookRepo.findById(id)
            .orElseThrow(()-> new IllegalArgumentException("No permission to delete book."));
        if (!roleUtil.isOwner(token,id)) {
            throw new IllegalArgumentException("No permission to delete book.");
        }
        List<Role> roles=roleRepo.findByBook(book);
        System.out.println("to delete book");
        bookRepo.deleteById(id);
        System.out.println("deleted book");
        // for (Role role : roles) {
        //     roleRepo.deleteById(role.getId());
        // }
    }

    @Override
    public BookDTO updateBookById(String token, Long id, BookUpdateRequest bookUpdReq) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!roleUtil.canEdit(token,id)) {
            throw new IllegalArgumentException("No permission to update book.");
        }
        book.setTitle(bookUpdReq.getTitle());
        book.setIsPublic(bookUpdReq.getIsPublic());
        bookRepo.save(book);
        return bookConverter.toDTO(book);
    }
    
    
}
