package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author syl
 * @since 2019-12-25
 */
public interface TbUserService extends IService<TbUser> {

    Boolean checkUsernameOrPhone(String data, Integer type);

    void sendCode(String phone);

    void register(TbUser user, String code);

    UserDTO queryUserByUsernameAndPassword(String username, String password);
}
