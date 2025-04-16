package com.huex.bamboohub.service;

import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.dao.Follow;
import com.huex.bamboohub.dao.FollowRepo;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceCache {
    @Autowired private UserConverter userConverter;
    @Autowired private FollowRepo followRepo;

    @Cacheable(value="userFollow",key="'user:'+#user.getId()")
    public List<UserDTO> getWhoIFollowByUser(User user) {
        List<Follow> follows=followRepo.findBySource(user);
        List<UserDTO> targetDTOs=new ArrayList<>();
        for (Follow follow : follows) {
            targetDTOs.add(userConverter.toDTO(follow.getTarget()));
        }
        return targetDTOs;
    }
}
