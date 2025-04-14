package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

public interface UserService {
    UserDTO register(RegisterReq regReq);
    UserDTO login(LoginReq loginReq);
    UserSimpleDTO getUserInfoByUsername(String username);
    UserDTO changePwd(String token, ChangePwdReq changePwdReq);
}
