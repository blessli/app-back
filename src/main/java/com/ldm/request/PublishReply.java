package com.ldm.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author lidongming
 * @ClassName PublishReply.java
 * @Description 发表回复
 * @createTime 2020年04月04日 04:23:00
 */
@Data
public class PublishReply {
    private int commentId;// 评论ID
    @NotEmpty
    private String toContent;// 被回复的内容
    @NotEmpty
    private String content;// 回复内容
    @Min(1)
    private int userId;// 回复者ID/用户ID
    @Min(1)
    private int toUserId;// 被回复者ID
    private int flag;// 0为活动，1为动态
    @Min(1)
    private int itemId;// 活动/动态ID
}
