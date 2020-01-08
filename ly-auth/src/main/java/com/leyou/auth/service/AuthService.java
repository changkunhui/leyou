package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author changkunhui
 * @date 2020/1/6 14:01
 */


@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String USER_ROLE = "SVIP";

    /**
     * 用户注册
     *
     * @param username
     * @param password
     */
    public void login(String username, String password, HttpServletResponse response) {


        try {
            //查询用户
            UserDTO user = userClient.queryUserByUsernameAndPassword(username, password);

            //生成userInfo
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), USER_ROLE);

            //生成token
            generateTokenAndSetCookie(response, userInfo);

        } catch (Exception e) {

            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * 生成token,并且设置到cookie
     *
     * @param response
     * @param userInfo
     */
    private void generateTokenAndSetCookie(HttpServletResponse response, UserInfo userInfo) {
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());

        //写入Cookie
        CookieUtils.newCookieBuilder()
                .value(token)
                .response(response)     //response,用户写入cookie
                .httpOnly(true)         //不允许JS操作cookie,防止XSS攻击
                .domain(prop.getUser().getCookieDomain())       //设置domain
                .name(prop.getUser().getCookieName())           //设置cookie的name
                .build();               //写入cookie
    }

    /**
     * 校验用户信息
     *
     * @return userInfo
     */
    public UserInfo verifyUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            //读取cookie
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            //解析token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);

            UserInfo userInfo = payload.getUserInfo();

            //校验token是否有效
            Boolean hasKey = redisTemplate.hasKey(payload.getId());
            if(hasKey != null && hasKey){
                //token已经无效,校验不成功
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }

            //获取token的过期时间
            Date expiration = payload.getExpiration();
            //获取刷新时间
            DateTime refreshTime = new DateTime(expiration.getTime()).minusMinutes(prop.getUser().getMinRefreshInterval());

            //判断是否到了刷新时间
            if (refreshTime.isBefore(System.currentTimeMillis())) {
                //重新生成token
                generateTokenAndSetCookie(response, userInfo);
            }

            return userInfo;

        } catch (Exception e) {
            //解析token抛出异常,证明token无效,返回401
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }

    /**
     * 用户退出,在token中加入失效标记
     *
     * @param request
     */
    public void logout(HttpServletRequest request,HttpServletResponse response) {
        try {
            //添加失效标记
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());

            //尝试解密token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);

            //获取距离失效时间的毫秒值
            long timeOut = payload.getExpiration().getTime() - System.currentTimeMillis();

            //大于5秒,才有放进去的意义
            if (timeOut > 5000) {
                //把标记放入redis中
                redisTemplate.boundValueOps(payload.getId()).set("", timeOut, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //删除cookie
            CookieUtils.deleteCookie(prop.getUser().getCookieName(),prop.getUser().getCookieDomain(),response);
        }
    }
}
