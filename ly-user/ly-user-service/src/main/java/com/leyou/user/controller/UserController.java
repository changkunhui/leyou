package com.leyou.user.controller;

import com.leyou.common.exceptions.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.service.TbUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;


@RestController
public class UserController {

    @Autowired
    private TbUserService userService;


    //  GET /check/{data}/{type}
    @GetMapping(value = "/check/{data}/{type}",name = "校验用户名或者手机号")
    public ResponseEntity<Boolean> checkUsernameOrPhone(
            @PathVariable String data,
            @PathVariable Integer type
    ){
        Boolean b = userService.checkUsernameOrPhone(data,type);
        return ResponseEntity.ok(b);
    }


    @PostMapping(value = "/code",name = "获取短信验证码")
    public ResponseEntity<Void> SendCode(@RequestParam String phone){
        userService.sendCode(phone);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/register",name = "用户注册的功能")
    public ResponseEntity<Void> register(@Valid TbUser tbUser, BindingResult result, @RequestParam("code") String code){
        if (result.hasErrors()) {
            String msg = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("|"));
            throw new LyException(400,msg);
        }
        userService.register(tbUser,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping(value = "/query",name = "根据参数中的用户名和密码查询指定用户")
    public ResponseEntity<UserDTO> queryUserByUsernameAndPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        UserDTO userDTO = userService.queryUserByUsernameAndPassword(username,password);
        return ResponseEntity.ok(userDTO);
    }


    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/checkTest/{data}/{type}")
    @ApiOperation(value = "校验用户名数据是否可用，如果不存在则可用")
    @ApiResponses({
            @ApiResponse(code = 200, message = "校验结果有效，true或false代表可用或不可用"),
            @ApiResponse(code = 400, message = "请求参数有误，比如type不是指定值")
    })
    public ResponseEntity<Boolean> checkTest(
            @ApiParam(value = "要校验的数据", example = "lisi") @PathVariable("data") String data,
            @ApiParam(value = "数据类型，1：用户名，2：手机号", example = "1") @PathVariable(value = "type") Integer type) {
        return ResponseEntity.ok(userService.checkUsernameOrPhone(data, type));
    }


}
