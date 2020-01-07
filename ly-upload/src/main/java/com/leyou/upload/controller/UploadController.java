package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author changkunhui
 * @date 2019/12/25 15:18
 */

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;


    @PostMapping(value = "/image",name = "上传图片到本地服务器")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(uploadService.upload(file));
    }


    @GetMapping(value = "/signature",name = "获取阿里云签名")
    public ResponseEntity<Map> signature(){
        return ResponseEntity.ok(uploadService.signature());
    }

}
