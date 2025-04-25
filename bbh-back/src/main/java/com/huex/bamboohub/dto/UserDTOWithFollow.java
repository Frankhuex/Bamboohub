package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserDTOWithFollow {
    private Long id;
    private Date createTime;
    private String username;
    private String nickname;

    private Boolean followed;
    private Date followTime;
}
