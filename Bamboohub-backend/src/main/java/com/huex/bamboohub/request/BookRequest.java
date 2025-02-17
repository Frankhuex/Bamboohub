package com.huex.bamboohub.request;

public class BookRequest {
    private String title;
    private Boolean isPublic;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Boolean getIsPublic() {
        return isPublic;
    }
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
