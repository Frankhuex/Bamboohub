package com.huex.bamboohub.dao;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="book")
public class Book {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @OneToOne
    @JoinColumn(name="start_para_id",nullable=true)
    private Paragraph startPara; //Blank paragraph created on book creation

    @OneToMany(mappedBy="book",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Paragraph> paragraphs=new ArrayList<>();




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Paragraph getStartPara() {
        return startPara;
    }

    public void setStartPara(Paragraph startPara) {
        this.startPara = startPara;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

}
