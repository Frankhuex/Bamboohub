package com.huex.bamboohub.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

@Component
public class ParagraphConverterImpl implements ParagraphConverter {
    
    // @Autowired
    // private BookRepo bookRepo;

    @Autowired
    private ParagraphRepo paraRepo;

    @Override
    public ParagraphDTO toDTO(Paragraph paragraph) {
        ParagraphDTO paraDTO = new ParagraphDTO();
        paraDTO.setId(paragraph.getId());
        paraDTO.setBookId(paragraph.getBook().getId());
        paraDTO.setAuthor(paragraph.getAuthor());
        paraDTO.setContent(paragraph.getContent());
        paraDTO.setPrevParaId(paragraph.getPrevParaId());
        paraDTO.setNextParaId(paragraph.getNextParaId());
        return paraDTO;
    }

    @Override
    public Paragraph toDAO(ParagraphRequest paraReq) throws IllegalArgumentException {
        Paragraph prevPara=paraRepo.findById(paraReq.getPrevParaId())
            .orElseThrow(() -> new IllegalArgumentException("Previous paragraph not found"));
        Book book=prevPara.getBook();

        Paragraph paragraph = new Paragraph();
        paragraph.setBook(book);
        paragraph.setAuthor(paraReq.getAuthor());
        paragraph.setContent(paraReq.getContent());
        paragraph.setPrevParaId(paraReq.getPrevParaId());
        paragraph.setNextParaId(prevPara.getNextParaId());
        return paragraph;
    }

    @Override
    public List<ParagraphDTO> toDTOs(List<Paragraph> paragraphs) {
        List<ParagraphDTO> paraDTOs = new ArrayList<>();
        for (Paragraph paragraph : paragraphs) {
            paraDTOs.add(toDTO(paragraph));
        }
        return paraDTOs;
    }
}
