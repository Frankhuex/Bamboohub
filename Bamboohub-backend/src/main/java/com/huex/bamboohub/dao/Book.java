package com.huex.bamboohub.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter @Setter
@Entity
@Table(name="book")
public class Book {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @Column(name="create_time")
    @CreatedDate
    private Date createTime;

    @Column(name="title")
    private String title;

    @OneToOne
    @JoinColumn(name="start_para_id",nullable=true)
    private Paragraph startPara; //Blank paragraph created on book creation

//    @Column(name="is_public")
//    private boolean isPublic;

    public enum Scope {ALLEDIT,ALLREAD,PRIVATE}
    @Enumerated(EnumType.STRING)
    @Column(name="scope")
    private Scope scope;

    //以下两个是必要的
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL,orphanRemoval=true)
    private List<Role> roles;

    @OneToMany(mappedBy="book",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Paragraph> paragraphs=new ArrayList<>();

    public Book() {}
    public Book(String title, Scope scope) {
        this.title = title;
        this.scope = scope;
    }

}
