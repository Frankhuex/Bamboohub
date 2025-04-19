package com.huex.bamboohub.service;
import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.request.*;

import java.util.List;

public interface UserService {
    UserDTOWithToken register(RegisterReq regReq);
    UserDTOWithToken login(LoginReq loginReq);
    UserDTOWithToken changePwd(String token, ChangePwdReq changePwdReq);

    UserDTO updateProfile(String token, UserUpdateReq userUpdateReq);


    UserDTO getUserById(Long id);
    List<UserDTO> searchUsersByAny(String query);
    FollowDTO followUser(String token, Long targetId);
    boolean unfollowUser(String token, Long targetId);
    FollowsDTO getWhoIFollow(String token);
}
