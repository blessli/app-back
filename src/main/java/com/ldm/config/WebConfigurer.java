package com.ldm.config;

import com.ldm.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
//public class WebConfigurer implements WebMvcConfigurer {
//        @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //添加自定义拦截器和拦截路径，此处对所有请求进行拦截，除了登录界面和登录接口
////        registry.addInterceptor(new LoginInterceptor())
////                .addPathPatterns("/**")
////                .excludePathPatterns("/login", "/user/login","/swagger-ui.html");
//        //addPathPatterns 用于添加拦截规则
//        //excludePathPatterns 用于排除拦截
//        registry
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//}
