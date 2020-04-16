package com.ldm.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AnnotationLogAspect: 注解式日志切面
 * 特定 JoinPoint 处的 Aspect 所采取的动作称为Advice
 */
@Slf4j
@Aspect
@Component
public class AnnotationLogAspect {
    /**
     * 声明切点，使用了注解 @Action 的方法会拦截生效
     */
    @Pointcut("@annotation(com.ldm.aop.Action)")
    public void annotationPointcut() {
    }

    /**
     * 声明前置通知，复用了@Pointcut注解定义的切点
     */
    @Async("asyncServiceExecutor")
    @Before("annotationPointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class); //获取方法上定义的注解，粒度更细更精确
        log.debug("注解式拦截，即将执行：" + action.name()); // 反射获得注解上的属性，然后做日志相关的记录操作

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("-------------------用户发起请求-----------------");
        // 记录下请求内容
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        //如果是表单，参数值是普通键值对。如果是application/json，则request.getParameter是取不到的。
        log.info("HTTP_HEAD Type : " + request.getHeader("Content-Type"));
        log.info("IP : " + request.getRemoteAddr());
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    /**
     * 声明后继通知，复用了@Pointcut注解定义的切点
     */
    @Async("asyncServiceExecutor")
    @After("annotationPointcut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.debug("注解式拦截，刚刚执行完：" + action.name());
    }

    /**
     * 异常通知 记录操作报错日志
     * @param joinPoint
     * @param e
     */
    @Async("asyncServiceExecutor")
    @AfterThrowing(value = "annotationPointcut()",throwing = "e")
    public void AfterThrowing(JoinPoint joinPoint, Throwable e){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.error("主人,主人,出bug啦!!!："+action.name());
    }
}
