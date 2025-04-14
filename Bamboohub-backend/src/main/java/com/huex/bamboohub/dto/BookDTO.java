package com.huex.bamboohub.dto;

import java.io.Serializable;

public class BookDTO {
    private Long id; //must be null for new books
    private String title;
    private Long startParaId;
    private Boolean isPublic;

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
    public Long getStartParaId() {
        return startParaId;
    }
    public void setStartParaId(Long startParaId) {
        this.startParaId = startParaId;
    }
    public Boolean getIsPublic() {
        return isPublic;
    }
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
