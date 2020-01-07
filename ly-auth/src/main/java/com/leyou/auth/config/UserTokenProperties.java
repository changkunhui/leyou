package com.leyou.auth.config;

import lombok.Data;

@Data
public class UserTokenProperties {
    /**
     * token过期时长
     */
    private int expire;
    /**
     * 存放token的cookie名称
     */
    private String cookieName;
    /**
     * 存放token的cookie的domain
     */
    private String cookieDomain;
    /**
     * 存放的是token刷新时间
     */
    private Integer minRefreshInterval;
}