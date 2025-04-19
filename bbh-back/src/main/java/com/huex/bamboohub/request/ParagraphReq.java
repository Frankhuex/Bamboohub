package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ParagraphReq {
    private String author;
    private String content;
    private Long prevParaId;


    
}
