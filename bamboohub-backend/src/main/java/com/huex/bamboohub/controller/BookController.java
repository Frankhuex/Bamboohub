package com.huex.bamboohub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.Response;
import static com.huex.bamboohub.Response.newFail;
import java.util.List;
@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/book")
    public Response<Long> addNewBook(@RequestBody BookRequest bookReq) {
        try { 
            return Response.newSuccess(bookService.addNewBook(bookReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{id}")
    public Response<BookDTO> getBookById(@PathVariable("id") Long id) {
        try { 
            return Response.newSuccess(bookService.getBookById(id));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/books")
    public Response<List<BookDTO>> getAllBooks() {
        try { 
            return Response.newSuccess(bookService.getAllBooks());
        } catch (Exception e) { 
            return newFail(e.getMessage());
        }
    }

    @DeleteMapping("/book/{id}")
    public Response<String> deleteBookById(@PathVariable("id") Long id) {
        try { 
            bookService.deleteBookById(id);
            return Response.newSuccess("Book deleted successfully");
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/book/{id}")
    public Response<BookDTO> updateBookById(@PathVariable("id") Long id, @RequestBody BookUpdateRequest bookUpdReq) {
        try { 
            return Response.newSuccess(bookService.updateBookById(id, bookUpdReq));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/bookIds")
    public Response<List<Long>> getAllBookIds() {
        try { 
            return Response.newSuccess(bookService.getAllBookIds());
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }
}
