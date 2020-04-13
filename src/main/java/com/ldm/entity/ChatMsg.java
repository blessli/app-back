package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ChatMsg.java
 * @Description TODO
 * @createTime 2020年04月07日 23:30:00
 */
@Data
public class ChatMsg {
    private String msg;
    private int toUserId;
    private String publishTime;
    private String toAvatar;
    private String toUserNickname;
    private int unReadCount;
}
