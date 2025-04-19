package com.huex.bamboohub.service;

import com.huex.bamboohub.converter.FollowConverter;
import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.dao.Follow;
import com.huex.bamboohub.dao.FollowRepo;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.FollowDTO;
import com.huex.bamboohub.dto.FollowsDTO;
import com.huex.bamboohub.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceCache {
    @Autowired private FollowConverter followConverter;
    @Autowired private FollowRepo followRepo;

    @Cacheable(value="userFollow",key="'user:'+#user.getId()")
    public FollowsDTO getWhoIFollowByUser(User user) {
        return followConverter.toFollowsDTO(followRepo.findBySource(user));
    }
}
