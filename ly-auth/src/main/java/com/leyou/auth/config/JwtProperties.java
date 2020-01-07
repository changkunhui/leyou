package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {

    //公钥和私钥路径
    private String pubKeyPath;
    private String priKeyPath;

    //公钥和私钥对象
    private PublicKey publicKey;
    private PrivateKey privateKey;

    //用户token相关属性,一定new一个对象,不会自动new的
    private UserTokenProperties user = new UserTokenProperties();



    @Override
    public void afterPropertiesSet() {
        try {
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException(e);
        }

    }
}