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

    @Column(name="is_public")
    private Boolean isPublic;


    //以下两个是必要的
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL,orphanRemoval=true)
    private List<Role> roles;

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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }


}
