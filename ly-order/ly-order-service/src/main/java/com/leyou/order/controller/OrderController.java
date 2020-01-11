package com.leyou.order.controller;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.service.TbOrderService;
import com.leyou.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author changkunhui
 * @date 2020/1/9 16:01
 */

@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private TbOrderService orderService;

    @PostMapping(name = "保存订单信息")
    public ResponseEntity<Long> saveOrder(@RequestBody OrderDTO order){
        Long orderId = orderService.saveOrder(order);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping(value = "/{id}",name = "查询订单信息")
    public ResponseEntity<OrderVO> findOrderById(@PathVariable("id")Long id){
        OrderVO orderVO = orderService.findOrderById(id);
        return ResponseEntity.ok(orderVO);
    }

    @GetMapping(value = "/url/{id}",name = "生成支付链接")
    public ResponseEntity<String> getPayUrl(@PathVariable("id")Long id){
        String codeUrl = orderService.getPayUrl(id);
        return ResponseEntity.ok(codeUrl);
    }

    @GetMapping(value = "/state/{id}",name = "查询支付状态")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id")Long id){
        Integer state = orderService.queryPayState(id);
        return ResponseEntity.ok(state);
    }

}
