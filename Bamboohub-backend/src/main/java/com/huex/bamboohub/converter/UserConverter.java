package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.PasswordUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserConverter {

    @Autowired
    private PasswordUtil passwordUtil;


    public UserDTOWithToken toDTOWithToken(User user, String token) {
        return new UserDTOWithToken(
                user.getId(),
                user.getCreateTime(),
                user.getUsername(),
                user.getNickname(),
                token
        );
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getCreateTime(),
                user.getUsername(),
                user.getNickname()
        );
    }

    public List<UserDTO> toDTOs(Iterable<User> users) {
        List<UserDTO> dtoList = new ArrayList<>();
        for (User user : users) {
            dtoList.add(toDTO(user));
        }
        return dtoList;
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
