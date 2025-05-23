package com.huex.bamboohub.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.converter.*;
import com.huex.bamboohub.util.*;
import org.springframework.util.StringUtils;

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
    @Autowired private CacheUtil cacheUtil;

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO addNewBook(String token, BookReq bookReq) throws IllegalArgumentException {
        User owner=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        if (owner==null) throw new IllegalArgumentException("Invalid token.");

        Book book=bookConverter.toDAO(bookReq);
        Book savedBook=bookRepo.save(book);

        Paragraph startPara=new Paragraph();
        startPara.setBook(savedBook);
        Paragraph savedStartPara=paraRepo.save(startPara);
        savedBook.setStartPara(savedStartPara);


        Paragraph endPara=new Paragraph();
        endPara.setBook(savedBook);
        Paragraph savedEndPara=paraRepo.save(endPara);
        savedBook.setEndPara(savedEndPara);

        savedStartPara.setNextParaId(savedEndPara.getId());
        savedEndPara.setPrevParaId(savedStartPara.getId());

        paraRepo.save(savedStartPara);
        paraRepo.save(savedEndPara);
        savedBook=bookRepo.save(savedBook);

        roleService.putRoleWithoutToken(owner, savedBook, Role.RoleType.OWNER);

        return bookConverter.toDTO(savedBook);
    }

    @Override
    @Cacheable(value="books", key="'book:'+#id")
    public BookDTO getBookById(String token, Long id) throws IllegalArgumentException {
        // Token can be empty
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!roleUtil.generalCanViewBook(token,book)) {
            throw new IllegalArgumentException("No permission to access book.");
        }

        return bookConverter.toDTO(book);
    }

    @Override
    // Books where you have a role
    public List<BookDTOWithRole> getMyBooksWithRole(String token) {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        
        List<Role> roles=roleRepo.findByUser(user);
        List<BookDTOWithRole> bookDTOs = new ArrayList<>();
        for (Role role : roles) {
            bookDTOs.add(bookConverter.toDTOWithRole(role.getBook(),role));
        }
        return bookDTOs;
    }

    @Override
    // Books where you have a role
    public List<BookDTOWithRole> getALLREADAndALLEDITBooksWithRole(String token) {
        List<Book> books=bookRepo.findByScopeIn(List.of(Book.Scope.ALLREAD, Book.Scope.ALLEDIT));
        User user=jwtUtil.parseUser(token).orElse(null);;;
        List<BookDTOWithRole> bookDTOs = new ArrayList<>();
        for (Book book: books) {
            addToFilteredBooks(bookDTOs, user, book);
        }

        return bookDTOs;
    }


    // Books where you have a role
    public List<BookDTO> getMyBooks(String token) {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;

        List<Role> roles=roleRepo.findByUser(user);
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Role role : roles) {
            bookDTOs.add(bookConverter.toDTO(role.getBook()));
        }
        return bookDTOs;
    }

    @Override
    public List<BookDTO> getBooksByScope(String token, Book.Scope scope) {
        // Books where you have a role && scope is private
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

    @Override
    public List<BookDTO> getNonPrivateBooks() {
        return bookConverter.toDTOs(bookRepo.findByScopeNot(Book.Scope.PRIVATE));
    }

    @Override
    public List<BookDTO> searchBooksByTitle(String token, String title) {
        List<Book> books=bookRepo.findByTitleContaining(title);
        List<Book> filteredBooks=new ArrayList<>();
        for (Book book: books) {
            if (roleUtil.generalCanSearchBook(token,book)) {
                filteredBooks.add(book);
            }
        }

        return bookConverter.toDTOs(filteredBooks);
    }

    @Override
    public List<BookDTOWithRole> searchBooksByTitleWithRole(String token, String title) {
        if (!StringUtils.hasText(title)) return new ArrayList<>();
        List<Book> books=bookRepo.findByTitleContaining(title);
        List<BookDTOWithRole> filteredBooks=new ArrayList<>();
        User user=jwtUtil.parseUser(token).orElse(null);
        for (Book book: books) {
            if (roleUtil.generalCanSearchBook(user,book)) {
                addToFilteredBooks(filteredBooks, user, book);
            }
        }
        return filteredBooks;
    }

    private void addToFilteredBooks(List<BookDTOWithRole> filteredBooks, User user, Book book) {
        if (user==null) filteredBooks.add(bookConverter.toDTOWithRole(book, null));
        else {
            Role role=roleRepo.findByUserAndBook(user, book).orElse(null);
            filteredBooks.add(bookConverter.toDTOWithRole(book,role));
        }
    }


    @Override
    @CacheEvict(value = "books", key="'book:'+#id")
    public boolean deleteBookById(String token, Long id) throws IllegalArgumentException {
        Book book=bookRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!roleUtil.isOwner(token,book)) {
            throw new IllegalArgumentException("No permission to delete book.");
        }
        bookRepo.deleteById(id);


        cacheUtil.clearCache(List.of("books","rolesOfBook","paraIdsOfBook","parasOfBook"),"bookId:"+book.getId());

        return true;
    }

    @Override
    @CacheEvict(value = "books", key="'book:'+#id")
    public BookDTO updateBookById(String token, Long id, BookUpdateReq bookUpdReq) throws IllegalArgumentException {
        Book book = bookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Book with id "+id+" not found."));
        if (!roleUtil.generalCanEditBook(token,book)) {
            throw new IllegalArgumentException("No permission to update book.");
        }
        if (StringUtils.hasText(bookUpdReq.getTitle()))
            book.setTitle(bookUpdReq.getTitle());
        if (bookUpdReq.getScope()!=null) {
            book.setScope(bookUpdReq.getScope());
        } else {
            throw new IllegalArgumentException("No update occurred.");
        }
        bookRepo.save(book);
        return bookConverter.toDTO(book);
    }


}
