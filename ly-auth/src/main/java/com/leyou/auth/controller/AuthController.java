package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author changkunhui
 * @date 2020/1/6 13:46
 */

@RestController
public class AuthController {


    @Autowired
    private AuthService authService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;


    @PostMapping(value = "/login",name = "用户登录的功能")
    public ResponseEntity<Void> login(@RequestParam String username,@RequestParam String password){
        authService.login(username,password,response);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/verify", name = "验证用户信息")
    public ResponseEntity<UserInfo> verifyUser(){
        UserInfo userInfo= authService.verifyUser(request,response);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping(value = "/logout", name = "用户退出")
    public ResponseEntity<Void> logout(){
        authService.logout(request,response);
        return ResponseEntity.ok().build();
    }

}
