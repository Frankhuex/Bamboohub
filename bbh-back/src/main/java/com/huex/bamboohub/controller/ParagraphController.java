package com.huex.bamboohub.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@RestController
public class ParagraphController {
    @Autowired
    private ParagraphService paragraphService;

    @GetMapping("/book/{bookId}/paragraphs")
    public Response<List<ParagraphDTO>> getParagraphsByBookId(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("bookId") Long bookId) {
        try {
            return Response.newSuccess(paragraphService.getParagraphsByBookId(token,bookId));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

//    @GetMapping("/book-title/{title}/paragraphs")
//    public Response<List<ParagraphDTO>> getParagraphsByTitle(@RequestHeader("Authorization") String token, @PathVariable("title") String title) {
//        try {
//            return Response.newSuccess(paragraphService.getParagraphsByBookTitle(token,title));
//        } catch (Exception e) {
//            return Response.newFail(e.getMessage());
//        }
//    }

    @GetMapping("/book/{bookId}/paraIds")
    public Response<List<Long>> getParaIdsByBookId(@RequestHeader("Authorization") String token, @PathVariable("bookId") Long bookId) {
        try {
            return Response.newSuccess(paragraphService.getParaIdsByBookId(token,bookId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PostMapping("/paragraph")
    public Response<ParagraphDTO> addNewParagraph(@RequestHeader(value="Authorization", required=false) String token, @RequestBody ParagraphReq paraReq) {
        try { 
            return Response.newSuccess(paragraphService.addNewParagraph(token, paraReq));
        } catch (Exception e) { 
            return Response.newFail(e.getMessage());
        }
    }

    @DeleteMapping("/paragraph/{id}")
    public Response<Boolean> deleteParagraph(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try {
            paragraphService.deleteParagraphById(token,id);
            return Response.newSuccess(true);
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraph/{id}")
    public Response<ParagraphDTO> getParagraphById(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.getParagraphById(token,id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/paragraph/{id}")
    public Response<ParagraphDTO> updateParagraphById(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id, @RequestBody ParagraphUpdateReq paraUpdReq)
    {
        try {
            return Response.newSuccess(paragraphService.updateParagraphById(token,id,paraUpdReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PostMapping("/paragraph/{id}/moveUp")
    public Response<ParagraphDTO> moveUpParagraphById(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.moveUpParagraphById(token,id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PostMapping("/paragraph/{id}/moveDown")
    public Response<ParagraphDTO> moveDownParagraphById(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.moveDownParagraphById(token,id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraph/{id}/getBook")
    public Response<BookDTO> getBookByParagraphId(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("id") Long id) {
        try {
            return Response.newSuccess(paragraphService.getBookByParaId(token,id));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraphs")
    public Response<List<ParagraphDTO>> getAllParagraphs(@RequestHeader("Authorization") String token ) {
        try {
            return Response.newSuccess(paragraphService.getAllParagraphs(token));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraphs/search")
    public Response<ParaSearchDTO> searchParagraphs(@RequestHeader(value="Authorization",required = false) String token, @RequestParam("query") String query) {
        try {
            return Response.newSuccess(paragraphService.searchParagraphsByAny(token,query));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

}
