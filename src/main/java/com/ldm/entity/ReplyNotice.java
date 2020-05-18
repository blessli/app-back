package com.ldm.entity;

import lombok.Data;

@Data
public class ReplyNotice {
    private int itemId;
    private int flag;// 0为活动,1为动态
    private String content;
    private String toContent;// 被回复的评论内容
    private String userNickname;
    private String avatar;
    private int userId;
    private String publishTime;
}
