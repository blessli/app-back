package com.ldm.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author lidongming
 * @ClassName PublishComment.java
 * @Description 发表评论
 * @createTime 2020年04月04日 04:21:00
 */
@Data
public class PublishComment {
    private int flag;// 0为活动，1为动态
    @Min(1)
    private int userId;// 用户ID
    @NotEmpty
    private String content;// 评论内容
    @Min(1)
    private int itemId;// 活动/动态ID
}
