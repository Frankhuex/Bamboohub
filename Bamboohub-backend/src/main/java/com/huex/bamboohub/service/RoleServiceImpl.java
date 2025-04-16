package com.huex.bamboohub.service;

import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dao.Role.RoleType;
import com.huex.bamboohub.converter.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Autowired private CacheManager cacheManager;

    @Override
    @CacheEvict(value="rolesOfBook", key="'bookId:'+#roleReq.getBookId()")
    public RoleDTO putRole(String token, RoleReq roleReq) throws IllegalArgumentException {
        //不能给被操作者赋予owner权限
        if (roleReq.getRoleType()==RoleType.OWNER) {
            throw new IllegalArgumentException("Cannot assign owner role");
        }

        // Check if employee exists
        User employee=userRepo.findById(roleReq.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if book exists
        Book book=bookRepo.findById(roleReq.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // Check permission
        if (roleUtil.canAdmin(token,book)) {throw new IllegalArgumentException("No permission to assign role");}

        // Check if employee already has role
        if (roleUtil.hasAnyRole(employee,book)) {throw new IllegalArgumentException("Alread has role");}

        Role newRole=roleConverter.toDAO(roleReq);
        Role savedRole=roleRepo.save(newRole);

        return roleConverter.toDTO(savedRole);
    }

    @Override
    public RoleDTO updateRole(String token, Long roleId, Role.RoleType newRoleType) {
        //不能给被操作者赋予owner权限
        if (newRoleType==RoleType.OWNER) {throw new IllegalArgumentException("Cannot assign owner role");}

        // Get role and check if it exists
        Role role=roleRepo.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Book book=role.getBook();
        User employee=role.getUser();

        // Check permission
        if (roleUtil.canAdmin(token,book)) {throw new IllegalArgumentException("No permission to assign role");}

        // Check if employee already has role
        if (!roleUtil.hasAnyRole(employee,book)) {throw new IllegalArgumentException("User"+employee.getId()+" has no role");}


        RoleType existingRoleType=role.getRoleType();
        if (existingRoleType==newRoleType) {
            //当被操作者的身份与新身份相同，无需修改
            throw new IllegalArgumentException("Duplicate role");
        }
        if (existingRoleType==RoleType.OWNER) {
            //当被操作者的身份是owner，禁止被修改
            throw new IllegalArgumentException("No permission to assign role");
        }
        if (existingRoleType==RoleType.ADMIN && !roleUtil.isOwner(token,book)) {
            //当被操作者的身份是admin，只有owner能修改他
            throw new IllegalArgumentException("No permission to assign role");
        }

        role.setRoleType(newRoleType);
        Role savedRole=roleRepo.save(role);
        return roleConverter.toDTO(savedRole);
    }

    @Override
    // Called another method to evict cache
    public boolean deleteRoleById(String token, Long roleId) {
        Role role=roleRepo.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        deleteRoleByBookIdAndEmployee(token, role.getBook().getId(), role.getUser());
        Cache cache=cacheManager.getCache("rolesOfBook");
        if (cache!=null) {
            cache.evict("bookId:"+role.getBook().getId());
        }
        return true;
    }

    @Override
    @CacheEvict(value="rolesOfBook", key="'bookId:'+#bookId")
    public boolean deleteRoleByBookIdAndUsername(String token, Long bookId, String username) throws IllegalArgumentException {
        User employee=userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        deleteRoleByBookIdAndEmployee(token, bookId, employee);
        return true;
    }


    @CacheEvict(value="rolesOfBook", key="'bookId:'+#bookId")
    public void deleteRoleByBookIdAndEmployee(String token, Long bookId, User employee) throws IllegalArgumentException {
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
                (employerRoleType==RoleType.ADMIN) //3.admin可删除非admin
        ) {
            roleRepo.deleteById(existingRole.getId());
        } else {
            throw new IllegalArgumentException("No permission to delete role");
        }

    }




    @Override
    @Cacheable(value="rolesOfBook", key="'bookId:'+#bookId")
    public RolesDTO getRolesByBookId(String token, Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to access book");
        }
        List<Role> roles=roleRepo.findByBook(book);
        return roleConverter.toRolesDTO(roles);
    }

    @Override
    public RoleType getOwnRoleByBookId(String token, Long bookId) {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (!roleUtil.hasAnyRole(token,book)) {
            throw new IllegalArgumentException("No role");
        }
        return roleUtil.getRoleType(token,book);
    }

    @Override
    public Role putRoleWithoutToken(User user, Book book, RoleType roleType) {
        // 查找现有的角色并更新，找不到则创建新角色
        Role role = roleRepo.findByUserAndBook(user, book)
                .map(existingRole -> {
                    existingRole.setRoleType(roleType);
                    return existingRole;
                })
                .orElseGet(() -> new Role(user, book, roleType));
        Cache cache=cacheManager.getCache("rolesOfBook");
        if (cache!=null) cache.evict("bookId:"+book.getId());
        return roleRepo.save(role);
    }
}
