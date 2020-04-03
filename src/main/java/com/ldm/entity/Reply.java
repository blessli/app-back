package com.ldm.entity;

import lombok.Data;

@Data
public class Reply {
    private int replyId;// 回复ID
    private int fromUserId;// 回复者ID
    private int toUserId;// 被回复者ID
    private int commentId;// 评论ID
    private String publishTime;// 发表时间
    private String content;// 回复内容
    private String fromNickname;// 回复者昵称
    private String toNickname;// 被回复者昵称
    private String avatar;// 头像
}
