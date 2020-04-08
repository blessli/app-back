package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName MsgSecCheck.java
 * @Description TODO
 * @createTime 2020年04月08日 23:45:00
 */
@Data
public class MsgSecCheck {
    /**
     * 0:内容正常
     * 87014:内容含有违法违规内容
     */
    private int errcode;// 错误码

    /**
     * "ok":内容正常
     * "risky":内容含有违法违规内容
     */
    private String errmsg;// 错误信息
}
