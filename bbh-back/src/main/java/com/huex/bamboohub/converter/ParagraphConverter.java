package com.huex.bamboohub.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@Component
public class ParagraphConverter {
    
    // @Autowired
    // private BookRepo bookRepo;

    @Autowired
    private ParagraphRepo paraRepo;


    public ParagraphDTO toDTO(Paragraph paragraph) {
        return new ParagraphDTO(
                paragraph.getId(),
                paragraph.getCreateTime(),
                paragraph.getBook().getId(),
                paragraph.getAuthor(),
                paragraph.getContent(),
                paragraph.getPrevParaId(),
                paragraph.getNextParaId()
        );
    }

    public List<ParagraphDTO> toDTOs(List<Paragraph> paragraphs) {
        List<ParagraphDTO> paraDTOs = new ArrayList<>();
        for (Paragraph paragraph : paragraphs) {
            paraDTOs.add(toDTO(paragraph));
        }
        return paraDTOs;
    }

    public List<ParagraphDTO> toDTOs(Paragraph[] paragraphs) {
        List<ParagraphDTO> paraDTOs = new ArrayList<>();
        for (Paragraph paragraph : paragraphs) {
            paraDTOs.add(toDTO(paragraph));
        }
        return paraDTOs;
    }


    public Paragraph toDAO(ParagraphReq paraReq) throws IllegalArgumentException {
        Paragraph prevPara=paraRepo.findById(paraReq.getPrevParaId())
            .orElseThrow(() -> new IllegalArgumentException("Previous paragraph not found"));
        Book book=prevPara.getBook();

//        Paragraph para=new Paragraph();
//        para.setBook(book);
//        para.setAuthor(paraReq.getAuthor());
//        para.setContent(paraReq.getContent());
//        para.setPrevParaId(paraReq.getPrevParaId());
//        para.setNextParaId(prevPara.getNextParaId());
//        return para;

        return new Paragraph(
                book,
                paraReq.getAuthor(),
                paraReq.getContent(),
                paraReq.getPrevParaId(),
                prevPara.getNextParaId()
        );
    }



}
