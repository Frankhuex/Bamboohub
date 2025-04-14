package com.huex.bamboohub.util;
import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.dao.*;

import com.huex.bamboohub.dto.RolesDTO;
import com.huex.bamboohub.dto.UserSimpleDTO;
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

    @Cacheable(value="rolesOfBook",key="'bookId:'+#book.getId()")
    public RolesDTO getRolesByBookIdWithoutToken(Book book) throws IllegalArgumentException {
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

    public Role putRole(User user, Book book, RoleType roleType) {
        // 查找现有的角色并更新，找不到则创建新角色
        Role role = roleRepo.findByUserAndBook(user, book)
            .map(existingRole -> {
                existingRole.setRoleType(roleType);
                return existingRole;
            })
            .orElseGet(() -> new Role(user, book, roleType));

        // 保存角色并返回
        return roleRepo.save(role);
    }

    public RoleType getRoleType(Long userId, Long bookId) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
            Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book id"));
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean isRole(Long userId, Long bookId, RoleType roleType) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
            Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book id"));
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType() == roleType;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isOwner(Long userId, Long bookId) {
        return isRole(userId, bookId, RoleType.OWNER);
    }

    public Boolean isAdmin(Long userId, Long bookId) {
        return isRole(userId, bookId, RoleType.ADMIN);
    }

    public Boolean isEditor(Long userId, Long bookId) {
        return isRole(userId, bookId, RoleType.EDITOR);
    }

    public Boolean isViewer(Long userId, Long bookId) {
        return isRole(userId, bookId, RoleType.VIEWER);
    }

    public Boolean hasAnyRole(Long userId, Long bookId) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
            Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book id"));
            return roleRepo.existsByUserAndBook(user, book);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean hasRoleButNot(Long userId, Long bookId, RoleType roleType) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
            Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book id"));
            Role role = roleRepo.findByUserAndBook(user, book).orElseThrow(() -> new IllegalArgumentException("User does not have role in book"));
            return role.getRoleType()!= roleType;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean canView(Long userId, Long bookId) {
        return hasAnyRole(userId, bookId);
    }

    public Boolean canEdit(Long userId, Long bookId) {
        return hasRoleButNot(userId, bookId, RoleType.VIEWER);
    }

    public Boolean canAdmin(Long userId, Long bookId) {
        return isRole(userId, bookId, RoleType.ADMIN) || isRole(userId, bookId, RoleType.OWNER);
    }

    public Boolean isEditorOrViewer(Long userId, Long bookId) {
        return isEditor(userId, bookId) || isViewer(userId, bookId);
    }



    public RoleType getRoleType(String token, Long bookId) {
        return getRoleType(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean isRole(String token, Long bookId, RoleType roleType) {
        return isRole(jwtUtil.parseUserId(token), bookId, roleType);
    }

    public Boolean isOwner(String token, Long bookId) {
        return isOwner(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean isAdmin(String token, Long bookId) {
        return isAdmin(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean isEditor(String token, Long bookId) {
        return isEditor(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean isViewer(String token, Long bookId) {
        return isViewer(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean hasAnyRole(String token, Long bookId) {
        return hasAnyRole(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean hasRoleButNot(String token, Long bookId, RoleType roleType) {
        return hasRoleButNot(jwtUtil.parseUserId(token), bookId, roleType);
    }

    public Boolean canView(String token, Long bookId) {
        return canView(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean canEdit(String token, Long bookId) {
        return canEdit(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean canAdmin(String token, Long bookId) {
        return canAdmin(jwtUtil.parseUserId(token), bookId);
    }

    public Boolean isEditorOrViewer(String token, Long bookId) {
        return isEditorOrViewer(jwtUtil.parseUserId(token), bookId);
    }


}
