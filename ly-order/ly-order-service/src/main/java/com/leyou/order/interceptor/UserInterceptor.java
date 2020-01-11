package com.leyou.order.interceptor;

import com.leyou.common.threadlocals.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现拦截,解析userId
 * @author changkunhui
 * @date 2020/1/9 15:24
 */

@Slf4j
public class UserInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String userId = request.getHeader("USER_ID");
            UserHolder.setUserId(userId);
            return true;
        } catch (Exception e) {
            log.error("【购物车服务】解析用户信息失败！", e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUserId();
    }
}
