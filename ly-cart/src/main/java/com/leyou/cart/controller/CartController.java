package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author changkunhui
 * @date 2020/1/8 16:34
 */

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping(name = "添加购物车")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/list",name = "查询用户的所有购物车数据")
    public ResponseEntity<List<Cart>> findCartList(){
        List<Cart> cartList = cartService.findCartList();
        return ResponseEntity.ok(cartList);
    }

    @PostMapping(value = "/list",name = "批量添加购物车(同步未登录的购物车)")
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> carts){
        cartService.addCartList(carts);
        return ResponseEntity.ok().build();
    }

    @PutMapping(name = "修改购物车中某个商品的数量")
    public ResponseEntity<Void> updateCart(@RequestParam("id")String skuId,@RequestParam("num")Integer num) {
        cartService.updateCart(skuId,num);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}",name = "删除购物车中的某个商品")
    public ResponseEntity<Void> deleteCart(@PathVariable("id")String skuId) {
        cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

}
