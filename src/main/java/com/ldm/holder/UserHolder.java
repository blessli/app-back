package com.ldm.holder;

public class UserHolder {
    /** 定义一个ThreadLocal 存储当前某个请求线程对应的登陆用户   */
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public static void addCurrentUser(User user){/** 把登录成功的用户存入到UserHolder*/
        if(users.get()== null){
            users.set(user);
        }
    }

    public static User getCurrentUser(){
        return users.get();
    }

    public static void removeCurrentUser() {
        users.remove();
    }
}
