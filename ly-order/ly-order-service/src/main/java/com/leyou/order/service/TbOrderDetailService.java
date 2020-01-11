package com.leyou.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.order.entity.TbOrderDetail;

import java.util.List;

/**
 * <p>
 * 订单详情表 服务类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbOrderDetailService extends IService<TbOrderDetail> {

    List<TbOrderDetail> findOvertimeOrderDetail();
}
