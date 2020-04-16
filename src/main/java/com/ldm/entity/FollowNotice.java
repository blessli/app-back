package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName FollowNotice.java
 * @Description TODO
 * @createTime 2020年04月15日 16:20:00
 */
@Data
public class FollowNotice {
    private int userId;
    private String avatar;
    private String userNickname;
    private String publishTime;
    private Boolean isMutualFollow;// 是否互关
}
