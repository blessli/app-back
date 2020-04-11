package com.ldm.response;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName MsgNotice.java
 * @Description TODO
 * @createTime 2020年04月11日 17:16:00
 */
@Data
public class MsgNotice {
    private int applyCount;
    private int agreeCount;
    private int replyCount;
    private int followCount;
}
