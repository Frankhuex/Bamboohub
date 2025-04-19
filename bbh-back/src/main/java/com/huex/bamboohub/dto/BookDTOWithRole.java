package com.huex.bamboohub.dto;

import com.huex.bamboohub.dao.Book;

import java.io.Serializable;
import java.util.Date;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.huex.bamboohub.dao.Book;
import com.huex.bamboohub.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTOWithRole {
    private Long id; //must be null for new books
    private Date createTime;
    private String title;
    private Long startParaId;
    private Long endParaId;
    private Book.Scope scope;
    private Role.RoleType roleType;
}
