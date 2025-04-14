package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;
import com.huex.bamboohub.util.PasswordUtil;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    @Autowired
    private PasswordUtil passwordUtil;


    public UserDTO toDTO(User user,String token) {
        return new UserDTO(
                user.getId(),
                user.getCreateTime(),
                user.getUsername(),
                user.getNickname(),
                token
        );
    }

    public UserSimpleDTO toSimpleDTO(User user) {
        return new UserSimpleDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname()
        );
    }

    public User toDAO(RegisterReq regReq) {
//        User user=new User();
//        user.setUsername(regReq.getUsername());
//        user.setPassword(passwordUtil.encryptPassword(regReq.getPassword()));
//        user.setNickname(regReq.getNickname());
//        return user;

        return new User(
                regReq.getUsername(),
                passwordUtil.encryptPassword(regReq.getPassword()),
                regReq.getNickname()
        );
    }
}
