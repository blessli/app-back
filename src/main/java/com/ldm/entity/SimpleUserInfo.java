package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName SimpleUserInfo.java
 * @Description TODO
 * @createTime 2020年04月10日 17:10:00
 */
@Data
public class SimpleUserInfo {
    private int userId;
    private String avatar;
    private String userNickname;
    private String publishTime;// 用于获取关注通知
}
