package com.huex.bamboohub.converter;
import com.huex.bamboohub.dao.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.PasswordUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
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

    public UserDTOWithFollow toDTOWithFollow(User user, boolean followed, Date followTime) {
        return new UserDTOWithFollow(
                user.getId(),
                user.getCreateTime(),
                user.getUsername(),
                user.getNickname(),
                followed,
                followTime
        );
    }

    public UserDTOWithFollow toDTOWithFollow(Follow follow) {
        User user=follow.getTarget();
        return new UserDTOWithFollow(
                user.getId(),
                user.getCreateTime(),
                user.getUsername(),
                user.getNickname(),
                true,
                follow.getCreateTime()
        );
    }

    public UserDTOWithFollow toDTOWithFollow(User user, Follow follow) {
        boolean followed;
        Date followTime;
        if (follow==null) {
            followed=false;
            followTime=null;
        } else {
            followed=true;
            followTime=follow.getCreateTime();
        }
        return new UserDTOWithFollow(
            user.getId(),
            user.getCreateTime(),
            user.getUsername(),
            user.getNickname(),
            followed,
            followTime
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
