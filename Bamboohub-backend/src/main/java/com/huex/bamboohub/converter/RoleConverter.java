package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;


@Component
public class RoleConverter {
    @Autowired private UserRepo userRepo;
    @Autowired private BookRepo bookRepo;

    public Role toDAO(RoleRequest roleReq) {
        User user=userRepo.findByUsername(roleReq.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book=bookRepo.findById(roleReq.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        
        Role role=new Role();
        role.setUser(user);
        role.setBook(book);
        role.setRoleType(roleReq.getRoleType());
        return role;
    }

    public RoleDTO toDTO(Role role) {
        RoleDTO roleDTO=new RoleDTO();
        roleDTO.setBookId(role.getBook().getId());
        roleDTO.setUserId(role.getUser().getId());
        roleDTO.setRoleType(role.getRoleType());
        return roleDTO;
    }
}
