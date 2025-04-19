package com.huex.bamboohub.request;
import com.huex.bamboohub.dao.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BookUpdateReq {
    private String title;
    private Book.Scope scope;



}
