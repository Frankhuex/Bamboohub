package com.huex.bamboohub.service;
import com.huex.bamboohub.converter.ParaRoleConverter;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.ParaRoleDTO;
import com.huex.bamboohub.dto.ParaRolesDTO;
import com.huex.bamboohub.request.ParaRoleReq;
import com.huex.bamboohub.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParaRoleServiceImpl implements ParaRoleService {
    @Autowired private ParagraphRepo paragraphRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private ParaRoleRepo paraRoleRepo;
    @Autowired private RoleUtil roleUtil;
    @Autowired private ParaRoleConverter paraRoleConverter;
    @Autowired private CacheManager cacheManager;


    @Override
    @CacheEvict(value="rolesOfParagraph", key="'paraId:'+#paraRoleReq.getParaId()")
    public ParaRoleDTO putParaRole(String token, ParaRoleReq paraRoleReq) {
        Paragraph paragraph=paragraphRepo.findById(paraRoleReq.getParaId())
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));
        Book book=paragraph.getBook();

        // Only OWNER and ADMIN can put paragraph role
        if (!roleUtil.canAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }
        ParaRole paraRole=paraRoleConverter.toDAO(paraRoleReq);
        ParaRole savedParaRole=paraRoleRepo.save(paraRole);
        return paraRoleConverter.toDTO(savedParaRole);
    }

    @Override
    public boolean deleteParaRoleById(String token, Long roleId) {
        ParaRole role=paraRoleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Book book=role.getParagraph().getBook();
        if (!roleUtil.canAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }
        paraRoleRepo.deleteById(roleId);
        Cache cache=cacheManager.getCache("rolesOfParagraph");
        if (cache!=null) cache.evict("paraId:"+role.getParagraph().getId());
        return true;
    }

    @Override
    public ParaRoleDTO updateParaRoleById(String token, Long roleId, ParaRole.RoleType newType) {
        ParaRole role=paraRoleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Book book=role.getParagraph().getBook();
        if (!roleUtil.canAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }
        role.setRoleType(newType);
        ParaRole savedRole=paraRoleRepo.save(role);
        Cache cache=cacheManager.getCache("rolesOfParagraph");
        if (cache!=null) cache.evict("paraId:"+role.getParagraph().getId());
        return paraRoleConverter.toDTO(savedRole);
    }

    @Override
    @Cacheable(value="rolesOfParagraph", key="'paraId:'+#paraId")
    public ParaRolesDTO getParaRolesByParaId(String token, Long paraId) {
        Paragraph paragraph=paragraphRepo.findById(paraId)
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));
        Book book=paragraph.getBook();
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to access paragraph");
        }
        List<ParaRole> paraRoles=paraRoleRepo.findByParagraph(paragraph);
        return paraRoleConverter.toParaRolesDTO(paraRoles);
    }

}
