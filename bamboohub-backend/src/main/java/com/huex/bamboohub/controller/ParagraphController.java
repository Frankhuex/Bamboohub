package com.huex.bamboohub.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.Response;

@RestController
public class ParagraphController {
    @Autowired
    private ParagraphService paragraphService;

    @GetMapping("/book/{bookId}/paragraphs")
    public Response<List<ParagraphDTO>> getParagraphsByBookId(@PathVariable("bookId") Long bookId) {
        try {
            return Response.newSuccess(paragraphService.getParagraphsByBookId(bookId));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book-title/{title}/paragraphs")
    public Response<List<ParagraphDTO>> getParagraphsByTitle(@PathVariable("title") String title) {
        try { 
            return Response.newSuccess(paragraphService.getParagraphsByBookTitle(title));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}/paraIds")
    public Response<List<Long>> getParaIdsByBookId(@PathVariable("bookId") Long bookId) {
        try {
            return Response.newSuccess(paragraphService.getParaIdsByBookId(bookId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PostMapping("/paragraph")
    public Response<Long> addNewParagraph(@RequestBody ParagraphRequest paraReq) {
        try { 
            return Response.newSuccess(paragraphService.addNewParagraph(paraReq));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @DeleteMapping("/paragraph/{id}")
    public Response<String> deleteParagraph(@PathVariable("id") Long id) {
        try {
            paragraphService.deleteParagraphById(id);
            return Response.newSuccess("Paragraph deleted successfully.");
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraph/{id}")
    public Response<ParagraphDTO> getParagraphById(@PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.getParagraphById(id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/paragraph/{id}")
    public Response<ParagraphDTO> updateParagraphById(@PathVariable("id") Long id, @RequestBody ParagraphUpdateRequest paraUpdReq)
    {
        try {
            return Response.newSuccess(paragraphService.updateParagraphById(id,paraUpdReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/paragraph/{id}/move-up")
    public Response<ParagraphDTO> moveUpParagraphById(@PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.moveUpParagraphById(id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/paragraph/{id}/move-down")
    public Response<ParagraphDTO> moveDownParagraphById(@PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.moveDownParagraphById(id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraph/{id}/get-book")
    public Response<BookDTO> getBookByParagraphId(@PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.getBookByParaId(id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraphs")
    public Response<List<ParagraphDTO>> getAllParagraphs() {
        try {
            return Response.newSuccess(paragraphService.getAllParagraphs());
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }
}
