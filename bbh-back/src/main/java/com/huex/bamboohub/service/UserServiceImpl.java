package com.huex.bamboohub.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;

import org.springframework.beans.factory.annotation.Autowired;
import com.huex.bamboohub.util.*;
import com.huex.bamboohub.converter.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserConverter userConverter;
    @Autowired private PasswordUtil passwordUtil;
    @Autowired private UserRepo userRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private FollowConverter followConverter;
    @Autowired private FollowRepo followRepo;
    @Autowired private CacheManager cacheManager;
    @Autowired private UserServiceCache userServiceCache;

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
    public UserDTOWithToken changePwd(String token, ChangePwdReq changePwdReq) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;

        if (!passwordUtil.matches(changePwdReq.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        user.setPassword(passwordUtil.encryptPassword(changePwdReq.getNewPassword()));
        userRepo.save(user);
        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        return userConverter.toDTOWithToken(user,newToken);
    }
    
    @Override
    public UserDTO updateProfile(String token, UserUpdateReq userUpdateReq) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;

        String oldUsername=user.getUsername(),
                oldNickname=user.getNickname(),
                newUsername=userUpdateReq.getUsername(),
                newNickname=userUpdateReq.getNickname();

        if (!StringUtils.hasText(newUsername) && !StringUtils.hasText(newNickname)) {
            throw new IllegalArgumentException("No update occurred.");
        }

        if (StringUtils.hasText(newUsername)) {
            if (userRepo.existsByUsername(newUsername) && !oldUsername.equals(newUsername))
                throw new IllegalArgumentException("Username already exists.");
            if (!oldUsername.equals(newUsername))
                user.setUsername(userUpdateReq.getUsername());
        }

        if (StringUtils.hasText(newNickname) && !oldNickname.equals(newNickname))
            user.setNickname(userUpdateReq.getNickname());

        Cache cache1=cacheManager.getCache("users");
        if (cache1!=null) cache1.evict("user:"+user.getId());
        Cache cache2=cacheManager.getCache("userFollow");
        if (cache2!=null) cache2.clear();

        return userConverter.toDTO(userRepo.save(user));
    }
    
/////////////////////////////////////////////////////

    @Override
    @Cacheable(value="users",key="'user:'+#id")
    public UserDTO getUserById(Long id) {
        return userConverter.toDTO(userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found.")));
    }

    @Override
    public List<UserDTO> searchUsersByAny(String query) throws IllegalArgumentException {
        return userConverter.toDTOs(userRepo.findByUsernameContainingOrNicknameContaining(query, query));
    }

    @Override
    public List<UserDTOWithFollow> searchUsersByAnyWithFollow(String token, String query) {
        if (!StringUtils.hasText(query)) {
            return new ArrayList<>();
        }
        List<User> users=userRepo.findByUsernameContainingOrNicknameContaining(query, query);
        List<UserDTOWithFollow> usersWithFollow=new ArrayList<>();
        User user=jwtUtil.parseUser(token).orElse(null);
        for (User target: users) {
            if (user==null) usersWithFollow.add(userConverter.toDTOWithFollow(user,null));
            else {
                Follow follow=followRepo.findBySourceAndTarget(user, target).orElse(null);
                usersWithFollow.add(userConverter.toDTOWithFollow(target,follow));
            }
        }
        usersWithFollow.sort((u1, u2) -> {
            String u1Name=u1.getUsername().contains(query)?u1.getUsername():u1.getNickname();
            String u2Name=u2.getUsername().contains(query)?u2.getUsername():u2.getNickname();
            return u1Name.length()-u2Name.length();
        });
        return usersWithFollow;
    }


    @Override
    public FollowDTO followUser(String token, Long targetId) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        
        User target=userRepo.findById(targetId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        Follow follow=followRepo.findBySourceAndTarget(user, target).orElse(new Follow(user, target));
        Follow savedFollow=followRepo.save(follow);

        Cache cache=cacheManager.getCache("userFollow");
        if (cache!=null) cache.clear();

        return followConverter.toDTO(savedFollow);
    }

    @Override
    public boolean unfollowUser(String token, Long targetId) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        
        User target=userRepo.findById(targetId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        followRepo.findBySourceAndTarget(user, target).ifPresent(follow -> followRepo.delete(follow));

        Cache cache=cacheManager.getCache("userFollow");
        if (cache!=null) cache.clear();

        return true;
    }

    @Override
    public FollowsDTO getWhoIFollow(String token) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        return followConverter.toFollowsDTO(followRepo.findBySource(user));
    }

    @Override
    public List<UserDTOWithFollow> getMyFollowing(String token) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElseThrow(()->new IllegalArgumentException("Invalid token"));;;
        List<Follow> follows=followRepo.findBySource(user);
        return follows.stream().map(follow->userConverter.toDTOWithFollow(follow)).toList();
    }

}
