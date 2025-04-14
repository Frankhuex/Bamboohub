package com.huex.bamboohub.dto;
import com.huex.bamboohub.dao.Role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @AllArgsConstructor
public class RoleDTO {
    private Long id;
    private Date createTime;
    private Long bookId;
    private Long userId;
    private RoleType roleType;


}
