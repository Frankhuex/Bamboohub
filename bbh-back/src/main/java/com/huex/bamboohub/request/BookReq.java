package com.huex.bamboohub.request;
import com.huex.bamboohub.dao.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter @Setter @AllArgsConstructor
public class BookReq {
    private String title;
//    private boolean isPublic;
    private Book.Scope scope;

}
