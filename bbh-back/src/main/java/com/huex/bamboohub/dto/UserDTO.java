package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserDTO {
    private Long id;
    private Date createTime;
    private String username;
    private String nickname;
}
