package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author changkunhui
 * @date 2020/1/8 16:38
 */

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String PREFIX = "ly:cart:";

    /**
     * 添加购物车数据
     * @param cart
     */
    public void addCart(Cart cart) {
        String skuId = cart.getSkuId().toString();
        //从thredLocal中获取userId
        String userId = UserHolder.getUserId();
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(PREFIX + userId);
        addCartInRedis(cart, boundHashOps);


    }


    /**
     * 查询用户的购物车数据
     * @return
     */
    public List<Cart> findCartList() {
        //从thredLocal中获取userId
        String userId = UserHolder.getUserId();
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(PREFIX + userId);

        //获取boundHashOps中的所有的值
        List<String> cartListJsonString = boundHashOps.values();

        if(CollectionUtils.isEmpty(cartListJsonString)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }

        //将集合中的每一个字符串转为对象
        List<Cart> cartList = cartListJsonString.stream().map(cartString -> {
            return JsonUtils.toBean(cartString, Cart.class);
        }).collect(Collectors.toList());

        return cartList;
    }

    /**
     * 批量添加购物车数据
     * @param carts
     */
    public void addCartList(List<Cart> carts) {

        //从thredLocal中获取userId
        String userId = UserHolder.getUserId();
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(PREFIX + userId);

        //依次添加数据到redis
        for (Cart cart : carts) {
            addCartInRedis(cart,boundHashOps);
        }
    }

    /**
     * 抽取出来的添加购物车数据到redis
     * @param cart
     * @param boundHashOps
     */
    private void addCartInRedis(Cart cart, BoundHashOperations<String, String, String> boundHashOps) {
        String skuId = cart.getSkuId().toString();
        //判断购物车中是否已经存在该商品
        if (boundHashOps.hasKey(skuId)){
            //如果已经存在,取出将数量并增加
            String cartJsonStr = boundHashOps.get(skuId);
            Cart cartRedis = JsonUtils.toBean(cartJsonStr, Cart.class);
            cartRedis.setNum(cartRedis.getNum() + cart.getNum());
            //重新设置到redis中
            boundHashOps.put(skuId,JsonUtils.toString(cartRedis));
        }else{
            //原来的购物车中不存在
            boundHashOps.put(skuId,JsonUtils.toString(cart));
        }
    }

    /**
     * 修改购物车中某个商品的数量
     * @param skuId
     * @param num
     */
    public void updateCart(String skuId, Integer num) {
        //从thredLocal中获取userId
        String userId = UserHolder.getUserId();
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(PREFIX + userId);

        //获取指定的购物商品
        String cartRedisStr = boundHashOps.get(skuId);
        if(StringUtils.isEmpty(cartRedisStr)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }

        //转为对象
        Cart cart = JsonUtils.toBean(cartRedisStr, Cart.class);
        cart.setNum(num);

        //再存到redis中
        boundHashOps.put(skuId,JsonUtils.toString(cart));
    }

    /**
     * 删除购物车中的某个商品
     * @param skuId
     */
    public void deleteCart(String skuId) {
        //从thredLocal中获取userId
        String userId = UserHolder.getUserId();
        redisTemplate.boundHashOps(PREFIX + userId).delete(skuId);

    }
}
