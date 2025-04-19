package com.huex.bamboohub.converter;

import com.huex.bamboohub.dao.Follow;
import com.huex.bamboohub.dto.FollowDTO;
import org.springframework.stereotype.Component;

@Component
public class FollowConverter {
    public FollowDTO toDTO(Follow follow) {
        return new FollowDTO(
                follow.getId(),
                follow.getCreateTime(),
                follow.getSource().getId(),
                follow.getTarget().getId()
        );
    }
}
