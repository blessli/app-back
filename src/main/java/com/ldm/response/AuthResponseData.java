package com.ldm.response;

import lombok.Data;

/**
 * @author lidongming
 * @ClassName AuthResponseData.java
 * @Description TODO
 * @createTime 2020年04月19日 16:07:00
 */
@Data
public class AuthResponseData {
    private String openId;
    private String token;
    private int userId;
    private String avatarUrl;
    private String userNickname;
}
