package com.huex.bamboohub.request;

public class RoleDeleteRequest {
    private String username;
    private Long bookId;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    
}
