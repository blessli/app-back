package com.ldm.entity;

import lombok.Data;

@Data
public class Comment {
    private int commentId;// 评论ID
    private int userId;// 用户ID
    private String userNickname;// 用户昵称
    private int replyCount;// 回复数
    private String content;// 评论内容
    private String publishTime;// 发表时间
    private int itemId;// 活动/动态ID
    private String avatar;// 评论者头像
    private int flag;// 0为活动，1为动态
}
