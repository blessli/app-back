package com.ldm.entity;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName loginCredential.java
 * @Description 登录凭证校验
 * @createTime 2020年04月08日 23:28:00
 */
@Data
public class LoginCredential {
    private String openId;// 用户唯一标识
    private String sessionKey;// 会话密钥
    private String unionid;// 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
    private int errcode;// 错误码
    private String errmsg;// 错误信息
}
