package com.leyou.item.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbCategory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 Mapper 接口
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-24
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {

    @Select("SELECT c.* FROM tb_category c INNER JOIN tb_category_brand cb ON c.id = cb.category_id WHERE cb.brand_id = #{brandId}")
    List<TbCategory> selectCategoryByBrandId(Long brandId);
}
