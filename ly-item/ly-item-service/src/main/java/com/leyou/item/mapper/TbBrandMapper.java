package com.leyou.item.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbBrand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 Mapper 接口
 * </p>
 *
 * @author SYL
 * @since 2019-12-24
 */
public interface TbBrandMapper extends BaseMapper<TbBrand> {

    @Select("SELECT b.* FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{id}")
    List<TbBrand> findBrandByCategoryId(@Param("id") Long id);
}
