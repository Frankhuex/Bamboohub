package com.huex.bamboohub.dto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huex.bamboohub.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RolesDTO {
    List<RoleDTO> owners;
    List<RoleDTO> admins;
    List<RoleDTO> editors;
    List<RoleDTO> viewers;

    public RolesDTO() {
        owners = new ArrayList<>();
        admins = new ArrayList<>();
        editors = new ArrayList<>();
        viewers = new ArrayList<>();
    }

    public void add(RoleDTO roleDTO) {
        Role.RoleType roleType = roleDTO.getRoleType();
        switch (roleType) {
            case OWNER:
                owners.add(roleDTO);
                break;
            case ADMIN:
                admins.add(roleDTO);
                break;
            case EDITOR:
                editors.add(roleDTO);
                break;
            case VIEWER:
                viewers.add(roleDTO);
                break;
            default:
                break;
        }
    }

}
