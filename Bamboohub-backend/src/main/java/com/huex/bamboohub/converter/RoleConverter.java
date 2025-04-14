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

    public RoleDTO toDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getCreateTime(),
                role.getBook().getId(),
                role.getUser().getId(),
                role.getRoleType()
        );
    }

    public Role toDAO(RoleReq roleReq) throws IllegalArgumentException {
        User user=userRepo.findByUsername(roleReq.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book=bookRepo.findById(roleReq.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("Book not found"));
//        Role role=new Role();
//        role.setUser(user);
//        role.setBook(book);
//        role.setRoleType(roleReq.getRoleType());
//        return role;

        return new Role(
                user,
                book,
                roleReq.getRoleType()
        );
    }


}
