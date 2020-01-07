package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author SYL
 * @since 2019-12-24
 */
public interface TbCategoryService extends IService<TbCategory> {

    List<CategoryDTO> findCategoryListByBrandId(Long brandId);

    void saveCategory(CategoryDTO categoryDTO);

    void updateCategory(CategoryDTO categoryDTO);

    void deleteCategoryAndBrand(String id);

    List<CategoryDTO> findCategoryListByCategoryIds(List<Long> ids);
}


