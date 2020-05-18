package com.ldm.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author lidongming
 * @ClassName UserInfo.java
 * @Description TODO
 * @createTime 2020年04月07日 15:22:00
 */
@Data
public class UserInfo {
    private int userId;// 返回
    @NotEmpty
    private String openId;
    @NotEmpty
    private String avatarUrl;// 微信头像
    @NotEmpty
    private String nickName;// 用户昵称
    private int gender;// 2是女,1是男,0是未知
}
