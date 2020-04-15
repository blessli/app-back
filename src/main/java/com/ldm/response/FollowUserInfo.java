package com.ldm.response;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName FollowUserInfo.java
 * @Description TODO
 * @createTime 2020年04月14日 22:11:00
 */
@Data
public class FollowUserInfo {
    private int userId;
    private String userNickname;
    private String avatar;
}
