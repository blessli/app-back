package com.ldm.response;

import lombok.Data;

@Data
public class FollowUserInfo {
    private int userId;
    private String userNickname;
    private String avatar;
}
