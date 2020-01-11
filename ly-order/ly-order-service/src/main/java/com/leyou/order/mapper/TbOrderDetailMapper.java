package com.leyou.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.order.entity.TbOrderDetail;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbOrderDetailMapper extends BaseMapper<TbOrderDetail> {

    @Select("SELECT od.sku_id,sum(od.num) num FROM tb_order_detail od,(SELECT * FROM tb_order WHERE status = 1 and TIMESTAMPDIFF(MINUTE,create_time,now()) > 60) o WHERE o.order_id = od.order_id GROUP BY od.sku_id")
    List<TbOrderDetail> findOvertimeOrderDetail();
}
