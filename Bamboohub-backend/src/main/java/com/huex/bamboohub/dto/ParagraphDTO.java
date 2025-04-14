package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.Date;

@Getter @Setter @AllArgsConstructor
public class ParagraphDTO {
    private Long id; //must be null for new paragraphs
    private Date createTime;
    private Long bookId;
    private String author;
    private String content;
    private Long prevParaId;
    private Long nextParaId;


}
