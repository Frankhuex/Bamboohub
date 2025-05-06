package com.huex.bamboohub.dto;
import com.huex.bamboohub.dao.Role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RoleDTO {
    private Long id;

    private Date createTime;
    private Long bookId;
    private UserDTO userDTO;
    private RoleType roleType;
}
