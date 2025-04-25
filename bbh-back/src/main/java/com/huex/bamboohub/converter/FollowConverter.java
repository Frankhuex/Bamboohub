package com.huex.bamboohub.converter;

import com.huex.bamboohub.dao.Follow;
import com.huex.bamboohub.dao.FollowRepo;
import com.huex.bamboohub.dto.FollowDTO;
import com.huex.bamboohub.dto.FollowsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class FollowConverter {
    @Autowired private UserConverter userConverter;
    public FollowDTO toDTO(Follow follow) {
        return new FollowDTO(
                follow.getId(),
                follow.getCreateTime(),
                follow.getSource().getId(),
                userConverter.toDTO(follow.getTarget())
        );
    }



    public FollowsDTO toFollowsDTO(Iterable<Follow> follows) {
        return new FollowsDTO(
                StreamSupport.stream(follows.spliterator(), true)
                       .map(this::toDTO)
                       .toList()
        );
    }


}
