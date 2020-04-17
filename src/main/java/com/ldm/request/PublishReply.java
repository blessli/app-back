package com.ldm.request;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName PublishReply.java
 * @Description 发表回复
 * @createTime 2020年04月04日 04:23:00
 */
@Data
public class PublishReply {
    private int commentId;// 评论ID
    private String fromContent;// 被回复的内容
    private String content;// 回复内容
    private int userId;// 回复者ID/用户ID
    private int toUserId;// 被回复者ID
    private int flag;// 0为活动，1为动态
    private int itemId;// 活动/动态ID
}
