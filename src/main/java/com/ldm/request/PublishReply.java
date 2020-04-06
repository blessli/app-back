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
    private String publishTime;// 发表时间
    private String content;// 回复内容
    private int fromUserId;// 回复者ID
    private int toUserId;// 被回复者ID
}
