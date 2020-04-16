package com.ldm.pojo;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName FollowOrNot.java
 * @Description TODO
 * @createTime 2020年04月14日 10:12:00
 */
@Data
public class FollowOrNot {
    private boolean flag;// 取消/关注
    private int userId;
    private int toUserId;
}
