package com.huex.bamboohub.dto;
import com.huex.bamboohub.dao.Role.RoleType;


public class RoleDTO {
    private Long bookId;
    private Long userId;
    private RoleType roleType;
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public RoleType getRoleType() {
        return roleType;
    }
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    
}
