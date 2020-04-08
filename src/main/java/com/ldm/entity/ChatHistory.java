package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName ChatHistory.java
 * @Description TODO
 * @createTime 2020年04月08日 15:46:00
 */
@Data
public class ChatHistory {
    private int userId;
    private String msg;
    private int toUserId;
    private String publishTime;
}
