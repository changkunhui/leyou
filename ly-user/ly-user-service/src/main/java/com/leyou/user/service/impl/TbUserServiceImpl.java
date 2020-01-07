package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;
import static com.leyou.user.constants.SmsConstants.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author syl
 * @since 2019-12-25
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 校验用户名和手机号码
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkUsernameOrPhone(String data, Integer type) {
        //判断传入的参数是否错误
        if(type != 1 && type != 2){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        if(type == 1){
            //用户名校验
            queryWrapper.lambda().eq(TbUser::getUsername,data);
        }
        if(type == 2){
            //手机号校验
            queryWrapper.lambda().eq(TbUser::getPhone,data);
        }

        //执行查询
        int count = this.count(queryWrapper);

        return count == 0;
    }

    /**
     * 发送短信验证码
     * @param phone
     */
    @Override
    public void sendCode(String phone) {

        //生成验证码,并且向rocketMQ中放入数据
        String numeric = RandomStringUtils.randomNumeric(4);
        System.err.println(numeric);
        String param = "{\"code\":\""+numeric+"\"}";

        Map<String,String> map = new HashMap<>();
        map.put(SMS_PARAM_KEY_PHONE,phone);
        map.put(SMS_PARAM_KEY_SIGN_NAME,"乐优商城");
        map.put(SMS_PARAM_KEY_TEMPLATE_CODE,"SMS_181857033");
        map.put(SMS_PARAM_KEY_TEMPLATE_PARAM,param);
        //rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME+":"+VERIFY_CODE_TAGS,map);

        //将验证码放到redis中,并且设置有失效时间为60秒
        redisTemplate.boundValueOps("ly-user-sms:"+phone).set(numeric, 180,TimeUnit.SECONDS);
    }


    /**
     * 用户注册
     * @param tbUser
     * @param code
     */
    @Override
    public void register(TbUser tbUser, String code) {
        //取出redis验证码
        String code_redis = redisTemplate.opsForValue().get("ly-user-sms:" + tbUser.getPhone());
        // 判断验证码是否已经过时
        if(code_redis == null){
            throw new LyException(ExceptionEnum.TIMEOUT_VERIFY_CODE);
        }

        // 验证验证码输入是否正确
        if(!StringUtils.equals(code_redis,code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //对密码进行加密
        String password_encode = passwordEncoder.encode(tbUser.getPassword());
        tbUser.setPassword(password_encode);

        //写入数据库
        boolean isSave = this.save(tbUser);
        if(!isSave){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据参数中的用户名和密码查询指定用户
     * @param username
     * @param password
     * @return
     */
    @Override
    public UserDTO queryUserByUsernameAndPassword(String username, String password) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getUsername,username);

        TbUser tbUser = this.getOne(queryWrapper);
        if(tbUser == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        //判断密码
        if (!passwordEncoder.matches(password,tbUser.getPassword())) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        return BeanHelper.copyProperties(tbUser,UserDTO.class);
    }
}
