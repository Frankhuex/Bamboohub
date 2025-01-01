package com.huex.bamboohub.request;

public class ParagraphRequest {
    private String author;
    private String content;
    private Long prevParaId;

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

    
}
