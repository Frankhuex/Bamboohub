package com.huex.bamboohub.service;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dao.Role.RoleType;
import com.huex.bamboohub.converter.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired private UserConverter userConverter;
    @Autowired private PasswordUtil passwordUtil;
    @Autowired private RoleRepo roleRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private BookRepo bookRepo;
    @Autowired private RoleUtil roleUtil;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RoleConverter roleConverter;

    @Override
    public void putRole(String token, RoleRequest roleReq) throws IllegalArgumentException {
        User employee=userRepo.findByUsername(roleReq.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Long employeeId=employee.getId();

        Book book=bookRepo.findById(roleReq.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Long bookId=book.getId();

        User employer=jwtUtil.parseUser(token);
        Long employerId=employer.getId();
        Role employerRole=roleRepo.findByUserAndBook(employer,book)
            .orElseThrow(() -> new IllegalArgumentException("No permission to assign role"));
        RoleType employerRoleType=employerRole.getRoleType();
        RoleType newRoleType=roleReq.getRoleType();

        if (employerRoleType!=RoleType.OWNER && employerRoleType!=RoleType.ADMIN) {
            //非admin或owner，禁止操作
            throw new IllegalArgumentException("No permission to assign role");    
        }
        if (newRoleType==RoleType.OWNER) {
            //不能给被操作者赋予owner权限
            throw new IllegalArgumentException("Cannot assign owner role");
        }

        if (!roleUtil.hasAnyRole(employeeId,bookId)) {
            //当被操作者是新人无身份
            Role newRole=roleConverter.toDAO(roleReq);
            roleRepo.save(newRole);
        } else {
            //当被操作者已有身份
            Role existingRole=roleRepo.findByUserAndBook(employee,book)
                .orElseThrow(() -> new IllegalArgumentException("Role not found")); //此报错为摆设，因为前面检查过
            RoleType existingRoleType=existingRole.getRoleType();
            if (existingRoleType==newRoleType) {
                //当被操作者的身份与新身份相同，无需修改
                throw new IllegalArgumentException("Duplicate role");
            }
            if (existingRoleType==RoleType.OWNER) {
                //当被操作者的身份是owner，禁止被修改
                throw new IllegalArgumentException("No permission to assign role");
            }
            if (existingRoleType==RoleType.ADMIN) {
                //当被操作者的身份是admin，只有owner能修改他
                if (employerRoleType==RoleType.OWNER) {
                    existingRole.setRoleType(newRoleType);
                    roleRepo.save(existingRole);
                } else  {
                    throw new IllegalArgumentException("No permission to assign role");
                }
            } else {
                //当被操作者的身份是editor或viewer，任何admin或owner都能修改他
                existingRole.setRoleType(newRoleType);
                roleRepo.save(existingRole);
            }
        }
    }

    @Override
    public void deleteRoleByBookIdAndUsername(String token, Long bookId, String username) throws IllegalArgumentException {
        User employee=userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Long employeeId=employee.getId();

        Book book=bookRepo.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        User employer=jwtUtil.parseUser(token);
        Long employerId=employer.getId();
        Role employerRole=roleRepo.findByUserAndBook(employer,book)
            .orElseThrow(() -> new IllegalArgumentException("No permission to delete role"));
        RoleType employerRoleType=employerRole.getRoleType();

        Role existingRole=roleRepo.findByUserAndBook(employee,book)
            .orElseThrow(() -> new IllegalArgumentException("Role not found")); 
        RoleType existingRoleType=existingRole.getRoleType();


        if (existingRoleType==RoleType.OWNER) {
            //不能删除owner
            throw new IllegalArgumentException("Cannot delete owner role");
        }
        if (existingRoleType==employerRoleType  //1.任何非owner都可删除自己
            ||
            employerRoleType==RoleType.OWNER //2.owner可删除非owner
            || 
            (employerRoleType==RoleType.ADMIN && existingRoleType!=RoleType.ADMIN) //3.admin可删除非admin
        ) {
            roleRepo.deleteById(existingRole.getId());
        } else {
            throw new IllegalArgumentException("No permission to delete role");
        }

    }

    @Override
    public RolesDTO getRolesByBookId(String token, Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (!book.getIsPublic() && !roleUtil.hasAnyRole(token,bookId)) {
                throw new IllegalArgumentException("No permission to view roles");
            }
        List<Role> roles=roleRepo.findByBook(book);
        UserSimpleDTO owner=new UserSimpleDTO();
        List<UserSimpleDTO> admins=new ArrayList<>();
        List<UserSimpleDTO> editors=new ArrayList<>();
        List<UserSimpleDTO> viewers=new ArrayList<>();
        for (Role role : roles) {
            RoleType roleType=role.getRoleType();
            if (roleType==RoleType.OWNER) {      
                owner=userConverter.toSimpleDTO(role.getUser());
            } else if (roleType==RoleType.ADMIN) {
                admins.add(userConverter.toSimpleDTO(role.getUser()));
            } else if (roleType==RoleType.EDITOR) {
                editors.add(userConverter.toSimpleDTO(role.getUser()));
            } else if (roleType==RoleType.VIEWER) {
                viewers.add(userConverter.toSimpleDTO(role.getUser()));
            }
        }
        RolesDTO rolesDTO=new RolesDTO();
        rolesDTO.setOwner(owner);
        rolesDTO.setAdmins(admins);
        rolesDTO.setEditors(editors);
        rolesDTO.setViewers(viewers);

        return rolesDTO;
    }

    @Override
    public RoleType getRoleByBookId(String token, Long bookId) {
        if (!roleUtil.hasAnyRole(token,bookId)) {
            throw new IllegalArgumentException("No permission to view roles");
        }
        return roleUtil.getRoleType(token,bookId);
    }
}
