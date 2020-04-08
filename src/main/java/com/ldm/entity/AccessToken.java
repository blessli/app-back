package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName AccessToken.java
 * @Description TODO
 * @createTime 2020年04月08日 23:15:00
 */
@Data
public class AccessToken {
    private String access_token;// 获取到的凭证
    private int expires_in;// 凭证有效时间，单位：秒。目前是7200秒之内的值。
    private int errcode;// 错误码
    private String errmsg;// 错误信息
}
