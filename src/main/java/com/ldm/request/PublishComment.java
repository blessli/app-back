package com.ldm.request;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName PublishComment.java
 * @Description 发表评论
 * @createTime 2020年04月04日 04:21:00
 */
@Data
public class PublishComment {
    private int flag;// 0为活动，1为动态
    private int userId;// 用户ID
    private String content;// 评论内容
    private int itemId;// 活动/动态ID
    private String publishTime;// 发布时间
}
