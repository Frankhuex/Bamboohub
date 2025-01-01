package com.huex.bamboohub.request;
import com.huex.bamboohub.dao.*;
public class RoleRequest {
    private String username;
    private Long bookId;
    private Role.RoleType roleType;

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
    public Role.RoleType getRoleType() {
        return roleType;
    }
    public void setRoleType(Role.RoleType roleType) {
        this.roleType = roleType;
    }

    
}
