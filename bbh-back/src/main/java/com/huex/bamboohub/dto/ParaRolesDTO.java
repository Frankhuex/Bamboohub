package com.huex.bamboohub.dto;

import com.huex.bamboohub.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.huex.bamboohub.dao.ParaRole;

@Getter @Setter
public class ParaRolesDTO {
    List<ParaRoleDTO> creators;
    List<ParaRoleDTO> contributors;


    public ParaRolesDTO() {
        creators = new ArrayList<>();
        contributors = new ArrayList<>();

    }

    public void add(ParaRoleDTO paraRoleDTO) {
        ParaRole.RoleType roleType = paraRoleDTO.getRoleType();
        switch (roleType) {
            case CREATOR:
                creators.add(paraRoleDTO);
                break;
            case CONTRIBUTOR:
                contributors.add(paraRoleDTO);
                break;

            default:
                break;
        }
    }

}
