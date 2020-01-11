package com.leyou.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.order.entity.TbOrder;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbOrderMapper extends BaseMapper<TbOrder> {

    @Update("update tb_order set status = 5 where status = 1 and TIMESTAMPDIFF(MINUTE,create_time,now()) > 60")
    void closeOvertimeOrder();
}
