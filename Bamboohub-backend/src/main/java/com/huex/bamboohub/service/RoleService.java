package com.huex.bamboohub.service;

import com.huex.bamboohub.request.RoleReq;
import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.dao.Role.RoleType;

public interface RoleService {
    void putRole(String token, RoleReq roleReq);
    void deleteRoleByBookIdAndUsername(String token, Long bookId, String username);
    RolesDTO getRolesByBookId(String token, Long bookId);
    RoleType getRoleByBookId(String token, Long bookId);
}
