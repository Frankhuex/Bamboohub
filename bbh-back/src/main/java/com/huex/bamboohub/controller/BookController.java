package com.huex.bamboohub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

import static com.huex.bamboohub.controller.Response.newFail;

import java.util.List;
@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/book")
    public Response<Long> addNewBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookReq) {
        try { 
            return Response.newSuccess(bookService.addNewBook(token,bookReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{id}")
    public Response<BookDTO> getBookById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {
        try { 
            return Response.newSuccess(bookService.getBookById(token,id));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/bookIds/private")
    public Response<List<Long>> getAllPrivateBookIds(@RequestHeader("Authorization") String token) {
        try { 
            return Response.newSuccess(bookService.getAllPrivateBookIds(token));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/bookIds/public")
    public Response<List<Long>> getAllPublicBookIds() {
        try { 
            return Response.newSuccess(bookService.getAllPublicBookIds());
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/books/private")
    public Response<List<BookDTO>> getAllBooks(@RequestHeader("Authorization") String token) {
        try { 
            return Response.newSuccess(bookService.getAllPrivateBooks(token));
        } catch (Exception e) { 
            return newFail(e.getMessage());
        }
    }

    @GetMapping("/books/public")
    public Response<List<BookDTO>> getAllPublicBooks() {
        try { 
            return Response.newSuccess(bookService.getAllPublicBooks());
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @DeleteMapping("/book/{id}")
    public Response<String> deleteBookById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {
        try { 
            bookService.deleteBookById(token,id);
            return Response.newSuccess("Book deleted successfully");
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/book/{id}")
    public Response<BookDTO> updateBookById(@RequestHeader("Authorization") String token, @PathVariable("id") Long id, @RequestBody BookUpdateRequest bookUpdReq) {
        try { 
            return Response.newSuccess(bookService.updateBookById(token,id,bookUpdReq));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    
}
