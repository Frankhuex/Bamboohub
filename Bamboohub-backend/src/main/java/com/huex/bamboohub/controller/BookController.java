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




//    @Autowired private ParagraphRepo paragraphRepo;
//    @Autowired private BookRepo bookRepo;
//    @PostMapping("/books/add-end-para")
//    public Response<List<Long>> addEndPara() {
//        try {
//            List<Book> books=bookRepo.findAll();
//            List<Long> endParaIds=new ArrayList<>();
//            for (Book book:books) {
//                Paragraph paragraph=book.getStartPara();
//                while (paragraph.getNextParaId()!=null) {
//                    paragraph=paragraphRepo.findById(paragraph.getNextParaId()).get();
//                }
//                Paragraph endPara=new Paragraph(
//                        book,
//                        "System",
//                        "End of the book"+book.getId(),
//                        paragraph.getId(),
//                        null
//                );
//                Paragraph savedEndPara=paragraphRepo.save(endPara);
//                paragraph.setNextParaId(savedEndPara.getId());
//                paragraphRepo.save(paragraph);
//                book.setEndPara(endPara);
//                bookRepo.save(book);
//                endParaIds.add(savedEndPara.getId());
//            }
//            return Response.newSuccess(endParaIds);
//        } catch (Exception e) {
//            return Response.newFail(e.getMessage());
//        }
//    }


    @PostMapping("/book")
    public Response<BookDTO> addNewBook(@RequestHeader("Authorization") String token, @RequestBody BookReq bookReq) {
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

    @GetMapping("books/search")
    public Response<List<BookDTO>> searchBooksByTitle(@RequestParam("title") String title) {
        try {
            return Response.newSuccess(bookService.searchBooksByTitle(title));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }





    @GetMapping("books/alledit")
    public Response<List<BookDTO>> getAllEditableBooks() {
        try {
            return Response.newSuccess(bookService.getBooksByScope("", Book.Scope.ALLEDIT));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("books/allread")
    public Response<List<BookDTO>> getAllReadableBooks() {
        try {
            return Response.newSuccess(bookService.getBooksByScope("", Book.Scope.ALLREAD));
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
    public Response<List<BookDTO>> getMyBooks(@RequestHeader("Authorization") String token) {
        try {
            return Response.newSuccess(bookService.getMyBooks(token));
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
