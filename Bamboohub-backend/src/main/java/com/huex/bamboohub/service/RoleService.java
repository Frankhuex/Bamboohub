package com.huex.bamboohub.service;

import com.huex.bamboohub.dao.Book;
import com.huex.bamboohub.dao.Role;
import com.huex.bamboohub.dao.User;
import com.huex.bamboohub.dto.RoleDTO;
import com.huex.bamboohub.request.RoleReq;
import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.dao.Role.RoleType;

public interface RoleService {
    RoleDTO putRole(String token, RoleReq roleReq);
    RoleDTO updateRole(String token, Long roleId, Role.RoleType roleType);
    boolean deleteRoleById(String token, Long roleId);
    boolean deleteRoleByBookIdAndUsername(String token, Long bookId, String username);
    RolesDTO getRolesByBookId(String token, Long bookId);
    RoleType getOwnRoleByBookId(String token, Long bookId);
    Role putRoleWithoutToken(User user, Book book, RoleType roleType);
    RolesDTO changeOwner(String token, Long bookId, Long targetId);
}
