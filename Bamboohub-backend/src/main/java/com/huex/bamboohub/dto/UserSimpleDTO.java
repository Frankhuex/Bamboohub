package com.huex.bamboohub.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @AllArgsConstructor
public class UserSimpleDTO {
    private Long id;
    private String username;
    private String nickname;
}
