package com.huex.bamboohub.util;
import com.huex.bamboohub.converter.RoleConverter;
import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.dao.*;

import com.huex.bamboohub.dto.RoleDTO;
import com.huex.bamboohub.dto.RolesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import com.huex.bamboohub.dao.Role.RoleType;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoleUtil {
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private RoleConverter roleConverter;



    public RoleType getRoleType(User user, Book book) {
        try {
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isRole(User user, Book book, RoleType roleType) {
        try {
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType()==roleType;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOwner(User user, Book book) {
        return isRole(user, book, RoleType.OWNER);
    }

    public boolean isAdmin(User user, Book book) {
        return isRole(user, book, RoleType.ADMIN);
    }

    public boolean isEditor(User user, Book book) {
        return isRole(user, book, RoleType.EDITOR);
    }

    public boolean isViewer(User user, Book book) {
        return isRole(user, book, RoleType.VIEWER);
    }

    public boolean hasAnyRole(User user, Book book) {
        try {
            return roleRepo.existsByUserAndBook(user, book);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasRoleButNot(User user, Book book, RoleType roleType) {
        try {
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType()!= roleType;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canView(User user, Book book) {
        return hasAnyRole(user, book);
    }

    public boolean canEdit(User user, Book book) {
        return hasRoleButNot(user, book, RoleType.VIEWER);
    }

    public boolean canAdmin(User user, Book book) {
        return isRole(user, book, RoleType.ADMIN) || isRole(user, book, RoleType.OWNER);
    }

    public boolean isEditorOrViewer(User user, Book book) {
        return isEditor(user, book) || isViewer(user, book);
    }



    public RoleType getRoleType(String token, Book book) {
        return getRoleType(jwtUtil.parseUser(token), book);
    }

    public boolean isRole(String token, Book book, RoleType roleType) {
        return isRole(jwtUtil.parseUser(token), book, roleType);
    }

    public boolean isOwner(String token, Book book) {
        return isOwner(jwtUtil.parseUser(token), book);
    }

    public boolean isAdmin(String token, Book book) {
        return isAdmin(jwtUtil.parseUser(token), book);
    }

    public boolean isEditor(String token, Book book) {
        return isEditor(jwtUtil.parseUser(token), book);
    }

    public boolean isViewer(String token, Book book) {
        return isViewer(jwtUtil.parseUser(token), book);
    }

    public boolean hasAnyRole(String token, Book book) {
        return hasAnyRole(jwtUtil.parseUser(token), book);
    }

    public boolean hasRoleButNot(String token, Book book, RoleType roleType) {
        return hasRoleButNot(jwtUtil.parseUser(token), book, roleType);
    }

    public boolean canView(String token, Book book) {
        return canView(jwtUtil.parseUser(token), book);
    }

    public boolean canEdit(String token, Book book) {
        return canEdit(jwtUtil.parseUser(token), book);
    }

    public boolean canAdmin(String token, Book book) {
        return canAdmin(jwtUtil.parseUser(token), book);
    }

    public boolean isEditorOrViewer(String token,Book book) {
        return isEditorOrViewer(jwtUtil.parseUser(token), book);
    }


}
