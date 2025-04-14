package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BookUpdateReq {
    private String title;
    private boolean isPublic;



}
