package com.huex.bamboohub.service;
import com.huex.bamboohub.converter.ParaRoleConverter;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.dto.ParaRoleDTO;
import com.huex.bamboohub.dto.ParaRolesDTO;
import com.huex.bamboohub.request.ParaRoleReq;
import com.huex.bamboohub.util.CacheUtil;
import com.huex.bamboohub.util.JwtUtil;
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
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CacheUtil cacheUtil;


    @Override
    @CacheEvict(value="rolesOfPara", key="'paraId:'+#paraRoleReq.getParaId()")
    public ParaRoleDTO putParaRole(String token, ParaRoleReq paraRoleReq) {
        Paragraph paragraph=paragraphRepo.findById(paraRoleReq.getParaId())
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));
        Book book=paragraph.getBook();

        // Only OWNER and ADMIN can put paragraph role
        if (!roleUtil.hasRoleCanAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }

        User user=jwtUtil.parseUser(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (paraRoleRepo.existsByUserAndParagraphAndRoleType(user,paragraph,paraRoleReq.getRoleType())) {
            throw new IllegalArgumentException("User already has this role for this paragraph");
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
        User user=jwtUtil.parseUser(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        // Only OWNER and ADMIN can delete paragraph role
        // Notice that you cannot delete yourself. It's not BookRole
        if (!roleUtil.hasRoleCanAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }
        paraRoleRepo.deleteById(roleId);
        cacheUtil.clearCache("rolesOfPara","paraId:"+role.getParagraph().getId());
        return true;
    }

    @Override
    public ParaRoleDTO updateParaRoleById(String token, Long roleId, ParaRole.RoleType newType) {
        ParaRole role=paraRoleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Book book=role.getParagraph().getBook();
        // Only OWNER and ADMIN can do this
        if (!roleUtil.hasRoleCanAdmin(token, book)) {
            throw new IllegalArgumentException("User not authorized to put paragraph role");
        }
        role.setRoleType(newType);
        ParaRole savedRole=paraRoleRepo.save(role);

        cacheUtil.clearCache("rolesOfPara","paraId:"+role.getParagraph().getId());
        return paraRoleConverter.toDTO(savedRole);
    }

    @Override
    @Cacheable(value="rolesOfPara", key="'paraId:'+#paraId")
    public ParaRolesDTO getParaRolesByParaId(String token, Long paraId) {
        Paragraph paragraph=paragraphRepo.findById(paraId)
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));
        if (!roleUtil.generalCanViewParagraph(token,paragraph)) {
            throw new IllegalArgumentException("No permission to access paragraph");
        }
        List<ParaRole> paraRoles=paraRoleRepo.findByParagraph(paragraph);
        return paraRoleConverter.toParaRolesDTO(paraRoles);
    }



}
