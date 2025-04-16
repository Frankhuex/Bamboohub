package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class UserDTOWithToken {
    private Long id;
    private Date createTime;
    private String username;
    private String nickname;
    private String token;

}

