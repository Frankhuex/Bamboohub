package com.huex.bamboohub.converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;



@Component
public class ParaRoleConverter {
    @Autowired private UserRepo userRepo;
    @Autowired private ParagraphRepo paraRepo;

    public ParaRoleDTO toDTO(ParaRole paraRole) {
        return new ParaRoleDTO(
                paraRole.getId(),
                paraRole.getCreateTime(),
                paraRole.getParagraph().getId(),
                paraRole.getUser().getId(),
                paraRole.getRoleType()
        );
    }

    public ParaRolesDTO toParaRolesDTO(Iterable<ParaRole> paraRoles) {
        ParaRolesDTO paraRolesDTO = new ParaRolesDTO();
        for (ParaRole paraRole : paraRoles) {
            paraRolesDTO.add(toDTO(paraRole));
        }
        return paraRolesDTO;
    }

    public ParaRole toDAO(ParaRoleReq paraRoleReq) throws IllegalArgumentException {
        User user=userRepo.findById(paraRoleReq.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Paragraph paragraph=paraRepo.findById(paraRoleReq.getParaId())
               .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));

        return new ParaRole(
                user,
                paragraph,
                paraRoleReq.getRoleType()
        );
    }


}
