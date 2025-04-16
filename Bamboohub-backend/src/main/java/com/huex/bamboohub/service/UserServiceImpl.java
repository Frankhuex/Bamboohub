package com.huex.bamboohub.service;

import org.springframework.stereotype.Service;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.util.*;
import com.huex.bamboohub.converter.*;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserConverter userConverter;
    @Autowired private PasswordUtil passwordUtil;
    @Autowired private UserRepo userRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private FollowConverter followConverter;
    @Autowired private FollowRepo followRepo;

    @Override
    public UserDTOWithToken register(RegisterReq regReq) throws IllegalArgumentException {
        if (userRepo.existsByUsername(regReq.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        User user = userConverter.toDAO(regReq);
        User savedUser = userRepo.save(user);
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername());
        return userConverter.toDTOWithToken(savedUser,token);
    }

    @Override
    public UserDTOWithToken login(LoginReq loginReq)  throws IllegalArgumentException {
        User user = userRepo.findByUsername(loginReq.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Username or password is incorrect."));
        if (!passwordUtil.matches(loginReq.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Username or password is incorrect.");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return userConverter.toDTOWithToken(user,token);
    }
    
    @Override
    public List<UserDTO> searchUsersByAny(String query)  throws IllegalArgumentException {
        return userConverter.toDTOs(userRepo.findByUsernameContainingOrNicknameContaining(query, query));
    }

    @Override
    public UserDTOWithToken changePwd(String token, ChangePwdReq changePwdReq)  throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token);
        if (!passwordUtil.matches(changePwdReq.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        user.setPassword(passwordUtil.encryptPassword(changePwdReq.getNewPassword()));
        userRepo.save(user);
        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        return userConverter.toDTOWithToken(user,newToken);
    }

    @Override
    public FollowDTO followUser(String token, Long targetId) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token);
        User target=userRepo.findById(targetId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        Follow follow=followRepo.findBySourceAndTarget(user, target).orElse(new Follow(user, target));
        Follow savedFollow=followRepo.save(follow);
        return followConverter.toDTO(savedFollow);
    }

    @Override
    public boolean unfollowUser(String token, Long targetId) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token);
        User target=userRepo.findById(targetId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        followRepo.findBySourceAndTarget(user, target).ifPresent(follow -> followRepo.delete(follow));
        return true;
    }
}
