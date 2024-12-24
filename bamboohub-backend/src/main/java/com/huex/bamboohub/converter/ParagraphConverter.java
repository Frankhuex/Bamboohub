package com.huex.bamboohub.converter;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import java.util.List;

public interface ParagraphConverter {
    public ParagraphDTO toDTO(Paragraph paragraph);
    public Paragraph toDAO(ParagraphRequest paraReq);
    public List<ParagraphDTO> toDTOs(List<Paragraph> paragraphs);
}
