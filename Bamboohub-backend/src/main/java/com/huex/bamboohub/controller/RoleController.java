package com.huex.bamboohub.controller;

import com.huex.bamboohub.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.request.RoleReq;
import com.huex.bamboohub.service.*;
import com.huex.bamboohub.util.JwtUtil;
import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.dao.Role.RoleType;

@RestController
public class RoleController {
    @Autowired private RoleService roleService;

    @PostMapping("/bookRole")
    public Response<RoleDTO> putRole(@RequestHeader("Authorization") String token, @RequestBody RoleReq roleReq) {
        try {
            return Response.newSuccess(roleService.putRole(token, roleReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("bookRole/{roleId}/{roleType}")
    public Response<RoleDTO> updateRole(@RequestHeader("Authorization") String token, @PathVariable Long roleId, @PathVariable RoleType roleType) {
        try {
            return Response.newSuccess(roleService.updateRole(token, roleId, roleType));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }
    @DeleteMapping("/bookRole/{roleId}")
    public Response<Boolean> deleteRole(@RequestHeader("Authorization") String token, @PathVariable Long roleId) {
        try {

            return Response.newSuccess(roleService.deleteRoleById(token, roleId));
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

    @GetMapping("/book/{bookId}/ownRole")
    public Response<RoleType> getOwnRole(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {
        try {
            return Response.newSuccess(roleService.getOwnRoleByBookId(token, bookId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

}
