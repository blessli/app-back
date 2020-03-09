package com.ldm.aop;

import org.springframework.stereotype.Service;

@Service
public class AnnotationService {

    @Action(name = "通过注解拦截")
    public void add() {
        System.out.println("执行了 add()...");
    }

    // say() 方法没有加 @Action 注解，因此拦截不会对 say() 生效
    public void say() {
        System.out.println("执行了 say()...");
    }
}