package com.ldm.service.user;

import com.ldm.entity.user.UserInfo;

public interface UserService {
    UserInfo selectUserCenter(String userId);
}
