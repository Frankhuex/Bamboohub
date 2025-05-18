package com.huex.bamboohub.dto;

public class ParagraphDTO {
    private Long id; //must be null for new paragraphs
    private Long bookId;
    private String author;
    private String content;
    private Long prevParaId;
    private Long nextParaId;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {        
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;        
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
