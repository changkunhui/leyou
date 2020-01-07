package com.leyou.test;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 测试JWT
 * @author changkunhui
 * @date 2020/1/5 18:31
 */
public class AuthTest {

    private String privateFilePath = "/Users/changkunhui/Workspaces/leyouWork/id_rsa";
    private String publicFilePath = "/Users/changkunhui/Workspaces/leyouWork/id_rsa.pub";

    @Test
    public void testJWT() throws Exception {

        /*//获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);

        //生成token
        String token = JwtUtils.generateTokenExpireInMinutes(new UserInfo(1L, "Jack", "guest"), privateKey, 5);
        System.err.println("token="+token);*/

        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcIkphY2tcIixcInJvbGVcIjpcImd1ZXN0XCJ9IiwianRpIjoiWkdNNU1tUmhZV0V0WkRFNFl5MDBPVGhoTFRrNFkySXRaak5qWm1VMlpHUTJPRGhqIiwiZXhwIjoxNTc4MjIxMjg3fQ.QtIxvFqwimCKLK5jPIEOd9Znw4lkdoDEqap-KOSPbEqijKN2HkO4-IZ0Y54Q3_o8Tqirffs_N-2y0E_1xtrtEC1k-dMTeVVuSR2qXl9K9vY6NFrRcKN5Bb-FFQwsxEayJpMuJbhpF32CLIqFgWPCm-rqaPOwaK6SnoGwaVZBMK_BALZWS2R_V-_Dh89--PKFEwpyHmQnjIv5q6qbPogh6qkck5d8LqeiZWHTOCVPkpf2sn1YyOlpe9AXCUnFuS9_QiwJrrfC94-lHvgN9VLgBnOXwOFtjo2kjqIWfYSMmIM0Mw-nf9WF1oeD9Yxe_IbDGZkXNqsyMfuwBRkSSMCFiQ";

        //获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);

        //解析token
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);

        System.err.println(payload);

    }
}