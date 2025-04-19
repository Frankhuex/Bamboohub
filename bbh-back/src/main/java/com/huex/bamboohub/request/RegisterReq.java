package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class RegisterReq {
    private String username;
    private String password;
    private String nickname;



}
