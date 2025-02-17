package com.huex.bamboohub.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huex.bamboohub.service.*;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.util.*;

import static com.huex.bamboohub.controller.Response.newFail;

import java.util.List;

import com.huex.bamboohub.converter.UserConverter;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserConverter userConverter;

    @PostMapping("/register")
    public Response<UserDTO> register(@RequestBody RegisterRequest regReq) {
        try {
            return Response.newSuccess(userService.register(regReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Response<UserDTO> login(@RequestBody LoginRequest loginReq) {
        try {
            return Response.newSuccess(userService.login(loginReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @GetMapping("/user/info")
    public Response<UserDTO> parseToken(@RequestHeader("Authorization") String token) {
        try {
            return Response.newSuccess(userConverter.toDTO(jwtUtil.parseUser(token),token));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @GetMapping("/username/{username}/info")
    public Response<UserSimpleDTO> getUserInfoByUsername(@PathVariable("username") String username) {
        try {
            return Response.newSuccess(userService.getUserInfoByUsername(username));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

    @PostMapping("/changepwd")
    public Response<UserDTO> changePwd(@RequestHeader("Authorization") String token, @RequestBody ChangePwdRequest changePwdReq) {
        try {
            return Response.newSuccess(userService.changePwd(token, changePwdReq));
        } catch (Exception e) {
            return newFail(e.getMessage());
        }
    }

}