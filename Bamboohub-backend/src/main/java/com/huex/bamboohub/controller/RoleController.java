package com.huex.bamboohub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.request.RoleDeleteRequest;
import com.huex.bamboohub.request.RoleRequest;
import com.huex.bamboohub.service.*;
import com.huex.bamboohub.util.JwtUtil;
import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.dao.Role.RoleType;

@RestController
public class RoleController {
    @Autowired private RoleService roleService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserConverter userConverter;

    @PutMapping("/role")
    public Response<String> putRole(@RequestHeader("Authorization") String token, @RequestBody RoleRequest roleReq) {
        try {
            roleService.putRole(token, roleReq);
            return Response.newSuccess("Role updated successfully");
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @DeleteMapping("/book/{bookId}/role/{username}")
    public Response<String> deleteRole(@RequestHeader("Authorization") String token, @PathVariable Long bookId, @PathVariable String username) {
        try {
            roleService.deleteRoleByBookIdAndUsername(token, bookId, username);
            return Response.newSuccess("Role deleted successfully");
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}/roles")
    public Response<RolesDTO> getRoles(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {
        try {
            return Response.newSuccess(roleService.getRolesByBookId(token, bookId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}/role")
    public Response<RoleType> getRole(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {
        try {
            return Response.newSuccess(roleService.getRoleByBookId(token, bookId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

}
