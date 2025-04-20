package com.huex.bamboohub.controller;

import com.huex.bamboohub.dao.Book;
import com.huex.bamboohub.dao.BookRepo;
import com.huex.bamboohub.dao.Paragraph;
import com.huex.bamboohub.dao.ParagraphRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

import static com.huex.bamboohub.controller.Response.newFail;

import java.util.ArrayList;
import java.util.List;
@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/book")
    public Response<BookDTO> addNewBook(@RequestHeader("Authorization") String token, @RequestBody BookReq bookReq) {
        try { 
            return Response.newSuccess(bookService.addNewBook(token,bookReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{id}")
    public Response<BookDTO> getBookById(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try { 
            return Response.newSuccess(bookService.getBookById(token,id));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/search")
    public Response<List<BookDTOWithRole>> searchBooksByTitleWithRole(@RequestHeader(value="Authorization",required=false) String token, @RequestParam("title") String title) {
        try {
            return Response.newSuccess(bookService.searchBooksByTitleWithRole(token,title));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }



    @GetMapping("books")
    public Response<List<BookDTO>> getAllNonPrivateBooks() {
        try {
            return Response.newSuccess(bookService.getNonPrivateBooks());
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/alledit")
    public Response<List<BookDTO>> getALLEDITBooks() {
        try {
            return Response.newSuccess(bookService.getBooksByScope("", Book.Scope.ALLEDIT));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/allread")
    public Response<List<BookDTO>> getALLREADBooks() {
        try {
            return Response.newSuccess(bookService.getBooksByScope("", Book.Scope.ALLREAD));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/allsearch")
    public Response<List<BookDTO>> getALLSEARCHBooks() {
        try {
            return Response.newSuccess(bookService.getBooksByScope("", Book.Scope.ALLSEARCH));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/private")
    public Response<List<BookDTO>> getPrivateBooks(@RequestHeader("Authorization") String token) {
        try {
            return Response.newSuccess(bookService.getBooksByScope(token, Book.Scope.PRIVATE));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/mine")
    public Response<List<BookDTOWithRole>> getMyBooksWithRole(@RequestHeader("Authorization") String token) {
        try {
            return Response.newSuccess(bookService.getMyBooksWithRole(token));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/plaza")
    public Response<List<BookDTOWithRole>> getALLREADAndALLEDITBooksWithRole(@RequestHeader(value="Authorization",required=false) String token) {
        try {
            return Response.newSuccess(bookService.getALLREADAndALLEDITBooksWithRole(token));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }



    @DeleteMapping("/book/{id}")
    public Response<Boolean> deleteBookById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {
        try {
            return Response.newSuccess(bookService.deleteBookById(token,id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/book/{id}")
    public Response<BookDTO> updateBookById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id, @RequestBody BookUpdateReq bookUpdReq) {
        try {
            return Response.newSuccess(bookService.updateBookById(token,id,bookUpdReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    
}
