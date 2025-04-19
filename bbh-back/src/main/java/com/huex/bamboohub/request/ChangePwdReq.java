package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ChangePwdReq {
    private String oldPassword;
    private String newPassword;

}
