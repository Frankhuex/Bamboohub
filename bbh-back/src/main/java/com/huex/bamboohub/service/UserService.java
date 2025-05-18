package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

public interface UserService {
    UserDTO register(RegisterRequest regReq);
    UserDTO login(LoginRequest loginReq);
    UserSimpleDTO getUserInfoByUsername(String username);
    UserDTO changePwd(String token, ChangePwdRequest changePwdReq);
}
