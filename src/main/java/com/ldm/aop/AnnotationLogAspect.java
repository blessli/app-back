package com.ldm.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
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

//    @Autowired
//    private AsyncService asyncService;
    /**
     * 声明切点，使用了注解 @Action 的方法会拦截生效
     */
    @Pointcut("@annotation(com.ldm.aop.Action)")
    public void annotationPointcut() {
    }

    /**
     * 声明前置通知，复用了@Pointcut注解定义的切点
     */
    @Before("annotationPointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.info("注解式拦截，即将执行：" + action.name()); // 反射获得注解上的属性，然后做日志相关的记录操作

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
    }

    /**
     * 声明后继通知，复用了@Pointcut注解定义的切点
     */
    @After("annotationPointcut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.info("注解式拦截，刚刚执行完：" + action.name());
    }

    /**
     * 异常通知 记录操作报错日志
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "annotationPointcut()",throwing = "e")
    public void AfterThrowing(JoinPoint joinPoint, Throwable e){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        log.error("主人,主人,出bug啦!!!："+action.name());
    }
}
