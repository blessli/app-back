package com.ldm.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AnnotationAopConfig: 注解式拦截 AOP配置类，相当于 spring-aop.xml
 */
@Configuration
@ComponentScan("com.ldm.aop")
@EnableAspectJAutoProxy  // @EnableAspectJAutoProxy开启了Spring对AspectJ的支持
public class AnnotationAopConfig {

}