package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName CommentNotice.java
 * @Description TODO
 * @createTime 2020年04月11日 13:54:00
 */
@Data
public class CommentNotice {
    private int itemId;
    private int flag;// 0为活动,1为动态
    private int isReply;// 1为是
    private String content;
    private String image;
    private String userNickname;
    private String avatar;
    private int userId;
    private String publishTime;
}
