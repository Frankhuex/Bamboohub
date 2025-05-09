package com.huex.bamboohub.controller;

import com.huex.bamboohub.dao.ParaRole;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ParaRoleController {
    @Autowired private ParaRoleService paraRoleService;

    @PostMapping("/paraRole")
    public Response<ParaRoleDTO> putParaRole(@RequestHeader("Authorization") String token, @RequestBody ParaRoleReq paraRoleReq) {
        try {
            return Response.newSuccess(paraRoleService.putParaRole(token, paraRoleReq));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @DeleteMapping("/paraRole/{paraRoleId}")
    public Response<Boolean> deleteParaRole(@RequestHeader("Authorization") String token, @PathVariable("paraRoleId") Long paraRoleId) {
        try {
            return Response.newSuccess(paraRoleService.deleteParaRoleById(token, paraRoleId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PutMapping("/paraRole/{paraRoleId}/{newType}")
    public Response<ParaRoleDTO> updateParaRole(@RequestHeader("Authorization") String token, @PathVariable("paraRoleId") Long paraRoleId, @PathVariable("newType") ParaRole.RoleType newType) {
        try {
            return Response.newSuccess(paraRoleService.updateParaRoleById(token, paraRoleId, newType));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/paragraph/{paraId}/paraRoles")
    public Response<ParaRolesDTO> getParaRole(@RequestHeader(value="Authorization",required=false) String token, @PathVariable("paraId") Long paraId) {
        try {
            return Response.newSuccess(paraRoleService.getParaRolesByParaId(token, paraId));
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

}
