package com.huex.bamboohub.dao;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="paragraph")
public class Paragraph {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="book_id",nullable=false)
    private Book book;

    @Column(name="author",nullable=true)
    private String author;

    @Column(name="content",nullable=true) 
    private String content;

    @Column(name="prev_para_id",nullable=true)
    private Long prevParaId;

    @Column(name="next_para_id",nullable=true)
    private Long nextParaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPrevParaId() {
        return prevParaId;
    }

    public void setPrevParaId(Long prevParaId) {
        this.prevParaId = prevParaId;
    }

    public Long getNextParaId() {
        return nextParaId;
    }

    public void setNextParaId(Long nextParaId) {
        this.nextParaId = nextParaId;
    }

    
}
