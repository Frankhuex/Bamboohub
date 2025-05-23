package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface ParagraphService {
    List<ParagraphDTO> getParagraphsByBookId(String token,Long bookId);
    //List<ParagraphDTO> getParagraphsByBookTitle(String token,String bookTitle);
    List<Long> getParaIdsByBookId(String token,Long bookId);
    ParagraphDTO addNewParagraph(String token, ParagraphReq paraReq);
    boolean deleteParagraphById(String token,Long id);
    ParagraphDTO getParagraphById(String token,Long id);
    ParagraphDTO updateParagraphById(String token, Long id, ParagraphUpdateReq paraUpdReq);
    ParagraphDTO moveUpParagraphById(String token,Long id);
    ParagraphDTO moveDownParagraphById(String token,Long id);
    BookDTO getBookByParaId(String token,Long paraId);
    List<ParagraphDTO> getAllParagraphs(String token);

    ParaSearchDTO searchParagraphsByAny(String token, String query);
    
}
