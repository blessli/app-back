package com.ldm.service.user;

import com.ldm.entity.UserInfo;

import java.util.List;

public interface UserService {
    UserInfo selectUserCenter(int userId);

    /**
     * 用户输入手机号，服务端发送验证码，服务端保存验证码，手机号，salt
     * @param phone
     * @return
     */
    boolean register(String phone);

    /**
     * 用户输入手机号，验证码，MD5加密过的密码
     * @param phone 手机号
     * @param code 验证码
     * @param password MD5(密码)
     * @return
     */
    boolean registerWithCode(String phone,String code,String password);

    /**
     * 用户输入手机号和MD5加密过的密码，登录成功返回token
     * @param phone 手机号
     * @param password MD5(密码)
     * @return
     */
    String login(String phone,String password);

    /**
     * 登录注销
     * @param token
     * @return
     */
    boolean logout(String token);

    /**
     * 关注活动或者用户
     * 0：用户，1：活动，2,：活动类型
     * @param followerId
     * @param followingId
     * @param followType
     * @return
     */
    boolean followActivityOrUser(int followerId,int followingId,int followType);

    /**
     * 获取关注的用户列表
     * @param userId
     * @return
     */
    List<Integer> selectFollowedUserList(int userId);
    /**
     * 获取用户关注的活动类型
     * @param userId
     * @return
     */
    List<String> selectActivityTypeList(int userId);

    /**
     * 获取某用户的粉丝列表
     * @param userId
     * @return
     */
    List<Integer> selectFanUserList(int userId);
}
