package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;


@Component
public class RoleConverter {
    @Autowired private UserRepo userRepo;
    @Autowired private BookRepo bookRepo;
    @Autowired private UserConverter userConverter;

    public RoleDTO toDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getCreateTime(),
                role.getBook().getId(),
                userConverter.toDTO(role.getUser()),
                role.getRoleType()
        );
    }

    public RolesDTO toRolesDTO(Iterable<Role> roles) {
        RolesDTO rolesDTO=new RolesDTO();
        for (Role role : roles) {
            rolesDTO.add(toDTO(role));
        }
        return rolesDTO;
    }

    public Role toDAO(RoleReq roleReq) throws IllegalArgumentException {
        User user=userRepo.findById(roleReq.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book=bookRepo.findById(roleReq.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return new Role(
                user,
                book,
                roleReq.getRoleType()
        );
    }


}
