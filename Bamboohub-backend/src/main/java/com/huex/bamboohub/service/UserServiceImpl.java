package com.huex.bamboohub.service;

import org.springframework.stereotype.Service;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.util.*;
import com.huex.bamboohub.converter.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserConverter userConverter;
    @Autowired private PasswordUtil passwordUtil;
    @Autowired private UserRepo userRepo;
    @Autowired private JwtUtil jwtUtil;


    @Override
    public UserDTO register(RegisterReq regReq) throws IllegalArgumentException {
        if (userRepo.existsByUsername(regReq.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        User user = userConverter.toDAO(regReq);
        User savedUser = userRepo.save(user);
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername());
        return userConverter.toDTO(savedUser,token);
    }

    @Override
    public UserDTO login(LoginReq loginReq) {
        User user = userRepo.findByUsername(loginReq.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Username or password is incorrect."));
        if (!passwordUtil.matches(loginReq.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Username or password is incorrect.");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return userConverter.toDTO(user,token);
    }
    
    @Override
    public UserSimpleDTO getUserInfoByUsername(String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
        UserSimpleDTO userSimpleDTO = userConverter.toSimpleDTO(user);
        return userSimpleDTO;
    }

    @Override
    public UserDTO changePwd(String token, ChangePwdReq changePwdReq) {
        User user=jwtUtil.parseUser(token);
        if (!passwordUtil.matches(changePwdReq.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        user.setPassword(passwordUtil.encryptPassword(changePwdReq.getNewPassword()));
        userRepo.save(user);
        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        return userConverter.toDTO(user,newToken);
    }
}
