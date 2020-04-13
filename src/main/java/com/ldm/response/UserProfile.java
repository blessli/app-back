package com.ldm.response;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName UserProfile.java
 * @Description TODO
 * @createTime 2020年04月12日 01:15:00
 */
@Data
public class UserProfile {
    private String avatar;
    private String userNickname;
    private long fanCount;
    private long focusCount;
}
