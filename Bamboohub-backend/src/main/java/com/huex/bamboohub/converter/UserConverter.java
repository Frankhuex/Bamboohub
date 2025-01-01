package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    @Autowired
    private PasswordUtil passwordUtil;

    //@Override
    // public UserDTO toDTO(User user) {
    //     UserDTO dto = new UserDTO();
    //     dto.setId(user.getId());
    //     dto.setUsername(user.getUsername());
    //     return dto;
    // }


    public UserDTO toDTO(User user,String token) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setToken(token);
        return dto;
    }


    public User toDAO(RegisterRequest regReq) {
        User user = new User();
        user.setUsername(regReq.getUsername());
        user.setPassword(passwordUtil.encryptPassword(regReq.getPassword()));
        user.setNickname(regReq.getNickname());
        return user;
    }
    
    public UserSimpleDTO toSimpleDTO(User user) {
        UserSimpleDTO dto = new UserSimpleDTO();
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        return dto;
    }
}
