package com.ldm.entity;

import lombok.Data;

@Data
public class ActivityMember {
    private int userId;
    private String userNickname;
    private String joinTime;
    private String avatar;
}
