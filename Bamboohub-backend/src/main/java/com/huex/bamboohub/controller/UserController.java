package com.huex.bamboohub.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;

import static com.huex.bamboohub.controller.Response.newFail;

import com.huex.bamboohub.converter.UserConverter;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserConverter userConverter;

    @PostMapping("/register")
    public Response<UserDTOWithToken> register(@RequestBody RegisterReq regReq) {
        try {
            return Response.newSuccess(userService.register(regReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Response<UserDTOWithToken> login(@RequestBody LoginReq loginReq) {
        try {
            return Response.newSuccess(userService.login(loginReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @GetMapping("/myInfo")
    public Response<UserDTOWithToken> parseToken(@RequestHeader("Authorization") String token) {
        try {
            return Response.newSuccess(userConverter.toDTOWithToken(jwtUtil.parseUser(token),token));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }


    @PostMapping("/changePwd")
    public Response<UserDTOWithToken> changePwd(@RequestHeader("Authorization") String token, @RequestBody ChangePwdReq changePwdReq) {
        try {
            return Response.newSuccess(userService.changePwd(token, changePwdReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }


    @GetMapping("/users/search")
    public Response<List<UserDTO>> searchUsers(@RequestParam("query") String query) {
        try {
            return Response.newSuccess(userService.searchUsersByAny(query));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }


    @PostMapping("/user/{userId}/follow")
    public Response<FollowDTO> followUser(@RequestHeader("Authorization") String token, @PathVariable("userId") long userId) {
        try {
            return Response.newSuccess(userService.followUser(token, userId));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/unfollow")
    public Response<Boolean> unfollowUser(@RequestHeader("Authorization") String token, @PathVariable("userId") long userId) {
        try {
            return Response.newSuccess(userService.unfollowUser(token, userId));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

}