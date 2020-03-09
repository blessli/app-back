package com.ldm.aop;

import java.lang.annotation.*;

/**
 * Action: 定义拦截规则的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {
    // 拦截规则的名称
    String name();
}