package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.upload.config.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author changkunhui
 * @date 2019/12/25 15:24
 */

@Service
public class UploadService {

    //支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/jpg","image/png", "image/jpeg", "image/bmp");


    /**
     * 上传文件到本地服务器
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        //图片信息校验
        //1.校验文件类型
        if(!suffixes.contains(file.getContentType())){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        //校验图片内容
        BufferedImage image = null;

        try {
            //如果是假图片,会出异常
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        if(image == null){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        String fileName = UUID.randomUUID() + file.getOriginalFilename();
        String imageUrl =  "/Users/changkunhui/Workspaces/leyouWork/imageServer/"+fileName;

        try {
            file.transferTo(new File(imageUrl));
            return "http://image.leyou.com/"+fileName;
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }

    @Autowired
    private OSSProperties properties;

    @Autowired
    private OSS client;

    public Map signature() {

        try {
            //从配置文件中获取超时时间
            long expireTime = properties.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, properties.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<String, Object>();
            respMap.put("accessId", properties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", properties.getDir());
            respMap.put("host", properties.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
            // respMap.put("expire", formatISO8601Date(expiration));
        }catch (Exception e){
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

    }

}
