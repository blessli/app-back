package com.ldm.entity.user;

import lombok.Data;

import java.util.List;
@Data
public class UserInfo {
    private String userId;
    private String userNickname;
    private String signature;
    private int focusCount;
    private int fanCount;
    private int friendCount;
    private String gender;
    private String avatar;
    private String address;
    private String birthday;
    private String userName;
    private List<Scan> scanList;
}
