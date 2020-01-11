package com.leyou.user.controller;

import com.leyou.common.utils.BeanHelper;
import com.leyou.user.dto.UserAddressDTO;
import com.leyou.user.entity.TbUserAddress;
import com.leyou.user.service.TbUserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author changkunhui
 * @date 2020/1/9 16:54
 */

@RestController
public class UserAddressController {

    @Autowired
    private TbUserAddressService addressService;


    @GetMapping(value = "/address/byId",name = "根据id查询用户的收货地址")
    public ResponseEntity<UserAddressDTO> findAddressById(@RequestParam("id") Long id){

        TbUserAddress userAddress = addressService.getById(id);
        UserAddressDTO userAddressDTO = BeanHelper.copyProperties(userAddress, UserAddressDTO.class);

        return ResponseEntity.ok(userAddressDTO);

    }

}
