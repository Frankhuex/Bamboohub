package com.huex.bamboohub.request;
import com.huex.bamboohub.dao.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class RoleReq {
    private Long userId;
    private Long bookId;
    private Role.RoleType roleType;
}
