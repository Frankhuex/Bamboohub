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
public class SecretController {
    @Autowired private ParagraphRepo paragraphRepo;
    @Autowired private BookRepo bookRepo;
//    @PostMapping("/secret/books/add-end-para")
    public Response<List<Long>> addEndPara() {
        try {
            List<Book> books=bookRepo.findAll();
            List<Long> endParaIds=new ArrayList<>();
            for (Book book:books) {
                Paragraph paragraph=book.getStartPara();
                while (paragraph.getNextParaId()!=null) {
                    paragraph=paragraphRepo.findById(paragraph.getNextParaId()).get();
                }
                Paragraph endPara=new Paragraph(
                        book,
                        "System",
                        "End of the book"+book.getId(),
                        paragraph.getId(),
                        null
                );
                Paragraph savedEndPara=paragraphRepo.save(endPara);
                paragraph.setNextParaId(savedEndPara.getId());
                paragraphRepo.save(paragraph);
                book.setEndPara(endPara);
                bookRepo.save(book);
                endParaIds.add(savedEndPara.getId());
            }
            return Response.newSuccess(endParaIds);
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

}
