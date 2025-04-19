package com.huex.bamboohub.dao;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@Table(name="paragraph")
public class Paragraph {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @Column(name="create_time")
    @CreationTimestamp
    private Date createTime;

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

    @OneToMany(mappedBy="paragraph",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<ParaRole> paraRoles=new ArrayList<>();

    public Paragraph() {}
    public Paragraph(Book book, String author, String content, Long prevParaId, Long nextParaId) {
        this.book = book;
        this.author = author;
        this.content = content;
        this.prevParaId = prevParaId;
        this.nextParaId = nextParaId;
    }
    
}
