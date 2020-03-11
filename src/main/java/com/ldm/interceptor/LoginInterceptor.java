package com.ldm.interceptor;

import com.ldm.holder.User;
import com.ldm.holder.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这样在方法想获取已登录的当前用户的信息的时候就不用传HttpSession 的参数了，
 * 直接获取这个静态类的get方法 如：UserHolder.getCurrentUser
 */
public class LoginInterceptor implements HandlerInterceptor {
    //请求之前
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        /** 拦截到用户的请求了 */
        String requestUrl = request.getRequestURL().toString();
        System.out.println("requestUrl:"+requestUrl);
        /** 判断session是否存在用户,如果存在说明用户已经登录了,应该放行 */
        User user = (User) request.getSession().getAttribute("user");
        if(user!=null){
            System.out.println("requestUrl:"+requestUrl+"->被放行！");
            /** 当前请求：每个请求是否都是一个线程   */
            UserHolder.addCurrentUser(user);
            return true;
        }else{
            // 重定向 到登录页面
            response.sendRedirect(request.getContextPath()+"/login");
            System.out.println("requestUrl:"+requestUrl+"->被拦截！");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        UserHolder.removeCurrentUser();   //请求完之后就要释放资源
    }
}
