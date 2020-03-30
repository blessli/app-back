package com.ldm.entity;

import lombok.Data;

@Data
public class ActivityReply {
    private int replyId;
    private String fromUserId;
    private String toUserId;
    private int commentId;
    private String replyTime;
    private String fromContent;
    private String toContent;
    private String fromNickname;
    private String toNickname;
    private String avatar;
}
