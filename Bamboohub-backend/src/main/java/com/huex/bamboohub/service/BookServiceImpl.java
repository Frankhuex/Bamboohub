package com.huex.bamboohub.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.converter.*;
import com.huex.bamboohub.util.*;

@Service
@CacheConfig(cacheNames = "books")
public class BookServiceImpl implements BookService {
    @Autowired private BookRepo bookRepo;
    @Autowired private ParagraphRepo paraRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private BookConverter bookConverter;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RoleUtil roleUtil;
    @Autowired private RoleService roleService;

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO addNewBook(String token, BookReq bookReq) throws IllegalArgumentException {
//        List<Book> books = bookRepo.findByTitle(bookReq.getTitle());
//        if (!CollectionUtils.isEmpty(books)) {
//            throw new IllegalArgumentException("Book title "+bookReq.getTitle()+" already exists.");
//        }
        
        Book book=bookConverter.toDAO(bookReq);
        Book savedBook=bookRepo.save(book);

        Paragraph startPara=new Paragraph();
        startPara.setBook(savedBook);
        Paragraph savedPara=paraRepo.save(startPara);
        
        savedBook.setStartPara(savedPara);
        paraRepo.save(savedPara);
        bookRepo.save(savedBook);

        User owner=jwtUtil.parseUser(token);
        roleService.putRoleWithoutToken(owner, savedBook, Role.RoleType.OWNER);

        return bookConverter.toDTO(savedBook);
    }

    @Override
    @Cacheable(value="books", key="'book:'+#id")
    public BookDTO getBookById(String token, Long id) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to access book.");
        }
        System.out.println(book);
        return bookConverter.toDTO(book);
    }

    //@Override
    public List<Long> getAllPrivateBookIds(String token) {
        List<Role> roles=roleRepo.findByUser(jwtUtil.parseUser(token));
        List<Long> bookIds = new ArrayList<>();
        for (Role role : roles) {
            bookIds.add(role.getBook().getId());
        }
        return bookIds;
    }

    //@Override
    public List<Long> getAllPublicBookIds() {
        List<Book> books = bookRepo.findByScopeIn(Arrays.asList(Book.Scope.ALLREAD, Book.Scope.ALLEDIT));
        List<Long> bookIds = new ArrayList<>();
        for (Book book : books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }

    //@Override
    public List<BookDTO> getMyBooks(String token) {
        List<Role> roles=roleRepo.findByUser(jwtUtil.parseUser(token));
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Role role : roles) {
            bookDTOs.add(bookConverter.toDTO(role.getBook()));
        }
        return bookDTOs;
    }

    @Override
    public List<BookDTO> getBooksByScope(String token, Book.Scope scope) {
        if (scope==Book.Scope.PRIVATE) {
            List<BookDTO> privateBookDTOs = new ArrayList<>();
            for (BookDTO bookDTO : getMyBooks(token)) {
                if (bookDTO.getScope()==Book.Scope.PRIVATE) {
                    privateBookDTOs.add(bookDTO);
                }
            }
            return privateBookDTOs;
        }
        return bookConverter.toDTOs(bookRepo.findByScope(scope));
    }

    //@Override
    public List<BookDTO> getAllPublicBooks() {
        List<Book> books = bookRepo.findByScopeIn(Arrays.asList(Book.Scope.ALLREAD, Book.Scope.ALLEDIT));
//        List<Book> books = bookRepo.findByScope(Book.Scope.ALLREAD);
//        books=bookRepo.findAll();
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            bookDTOs.add(bookConverter.toDTO(book));
        }
        return bookDTOs;
    }
    
    @Override
    @CacheEvict(value = "books", key="'book:'+#id")
    public boolean deleteBookById(String token, Long id) throws IllegalArgumentException {

        Book book=bookRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!roleUtil.isOwner(token,book)) {
            throw new IllegalArgumentException("No permission to delete book.");
        }
//        List<Role> roles=roleRepo.findByBook(book);
//        System.out.println("to delete book");
        bookRepo.deleteById(id);
//        System.out.println("deleted book");
        // for (Role role : roles) {
        //     roleRepo.deleteById(role.getId());
        // }
        return true;
    }

    @Override
    @CachePut(value = "books", key="'book:'+#id")
    public BookDTO updateBookById(String token, Long id, BookUpdateReq bookUpdReq) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to update book.");
        }
        book.setTitle(bookUpdReq.getTitle());
//        book.setPublic(bookUpdReq.isPublic());
        bookRepo.save(book);
        return bookConverter.toDTO(book);
    }
    
    
}
