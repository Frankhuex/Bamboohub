package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @AllArgsConstructor
public class UserDTO {
    private Long id;
    private Date createTime;
    private String username;
    private String nickname;
    private String token;

}

