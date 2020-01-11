package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 Mapper 接口
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbSkuMapper extends BaseMapper<TbSku> {

    @Update("update tb_sku set stock = stock - #{num} where id = #{id}")
    void stockMinus(@Param("id") Long skuId, @Param("num") Integer num);

    @Update("update tb_sku set stock = stock + #{num} where id = #{id}")
    void stockPlus(@Param("id") Long skuId, @Param("num") Integer num);
}
