package com.huex.bamboohub.service;

import com.huex.bamboohub.dao.ParaRole;
import com.huex.bamboohub.dao.Role;
import com.huex.bamboohub.dto.ParaRoleDTO;
import com.huex.bamboohub.dto.ParaRolesDTO;
import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.request.ParaRoleReq;
import com.huex.bamboohub.request.RoleReq;

public interface ParaRoleService {
    ParaRoleDTO putParaRole(String token, ParaRoleReq paraRoleReq);
    boolean deleteParaRoleById(String token, Long roleId);
    ParaRoleDTO updateParaRoleById(String token, Long roleId, ParaRole.RoleType newType);
    ParaRolesDTO getParaRolesByParaId(String token, Long paraId);
}
