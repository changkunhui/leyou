package com.leyou.gateway.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * @author changkunhui
 * @date 2020/1/6 17:07
 */

@Slf4j
@Component
public class AuthFilter extends ZuulFilter {


    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProperties;


    /**
     * 声明过滤的类型,前置,后置,异常,路由中
     * @return
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 过滤的级别
     * @return
     */
    @Override
    public int filterOrder() {
        return FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }


    /**
     * 过滤的条件(是否过滤),true进入run方法,false则不进入
     * @return
     */
    @Override
    public boolean shouldFilter() {

        //获取上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = ctx.getRequest();
        //获取访问的URI
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);

        //获取白名单
        List<String> allowPathList = filterProperties.getAllowPaths();

        for (String allowPath : allowPathList) {
            if(requestURI.startsWith(allowPath)){
                return false;
            }
        }
        return true;
    }


    /**
     * 真正执行过滤的逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        //获取上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = ctx.getRequest();
        //获取token
        String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());

        try {
            //解析token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);

            //获取用户
            UserInfo userInfo = payload.getUserInfo();

            //获取用户角色
            String role = userInfo.getRole();

            //获取当前URI
            String path = request.getRequestURI();

            //获取要访问的方法
            String method = request.getMethod();

            // TODO 判断权限，此处暂时空置，等待权限服务完成后补充
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(), role, method, path);
        } catch (Exception e) {
            //解析抛出异常,说明用户为未登录
            ctx.setResponseStatusCode(403);
            ctx.setSendZuulResponse(false);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }

        return null;
    }
}
