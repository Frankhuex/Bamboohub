package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LoginReq {
    private String username;
    private String password;


}
