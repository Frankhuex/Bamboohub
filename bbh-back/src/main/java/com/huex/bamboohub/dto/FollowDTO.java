package com.huex.bamboohub.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.huex.bamboohub.dao.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowDTO {
    private Long id;
    private Date createTime;
    private Long sourceId;
    private UserDTO target;
}
