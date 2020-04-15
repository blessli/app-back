package com.ldm.request;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName UserInfo.java
 * @Description TODO
 * @createTime 2020年04月07日 15:22:00
 */
@Data
public class UserInfo {
    private String openId;
    private String avatarUrl;// 微信头像
    private String userNickname;// 用户昵称
    private int gender;// 2是女,1是男
}
