package com.leyou.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.vo.OrderVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbOrderService extends IService<TbOrder> {

    Long saveOrder(OrderDTO order);

    OrderVO findOrderById(Long id);

    String getPayUrl(Long id);

    Integer queryPayState(Long id);

    void cleanOverTimeOrder();
}
