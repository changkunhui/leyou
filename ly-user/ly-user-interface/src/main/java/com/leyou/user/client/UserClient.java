package com.leyou.user.client;

import com.leyou.user.dto.UserAddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author changkunhui
 * @date 2020/1/6 13:38
 */


@FeignClient("user-service")
public interface UserClient {

    @GetMapping(value = "/query",name = "根据参数中的用户名和密码查询指定用户")
    UserDTO queryUserByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);


    @GetMapping(value = "/address/byId",name = "根据id查询用户的收货地址")
    UserAddressDTO findAddressById(@RequestParam("id") Long id);

}
