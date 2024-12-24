package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface ParagraphService {
    List<ParagraphDTO> getParagraphsByBookId(Long bookId);
    List<ParagraphDTO> getParagraphsByBookTitle(String bookTitle);
    List<Long> getParaIdsByBookId(Long bookId);
    Long addNewParagraph(ParagraphRequest paraReq);
    void deleteParagraphById(Long id);
    ParagraphDTO getParagraphById(Long id);
    ParagraphDTO updateParagraphById(Long id,ParagraphUpdateRequest paraUpdReq);
    ParagraphDTO moveUpParagraphById(Long id);
    ParagraphDTO moveDownParagraphById(Long id);
    BookDTO getBookByParaId(Long paraId);
    List<ParagraphDTO> getAllParagraphs();
}
