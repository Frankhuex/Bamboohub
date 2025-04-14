package com.huex.bamboohub.dto;

import java.io.Serializable;
import java.util.Date;

import com.huex.bamboohub.dao.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
public class BookDTO {
    private Long id; //must be null for new books
    private Date createTime;
    private String title;
    private Long startParaId;
    private Book.Scope scope;
}
