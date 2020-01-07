package com.leyou.item.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务类
 * </p>
 *
 * @author SYL
 * @since 2019-12-24
 */
public interface TbBrandService extends IService<TbBrand> {

    PageResult<BrandDTO> findPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrandAndCategory(BrandDTO brandDTO, List<Long> cids);

    void updateBrandAndCategory(BrandDTO brandDTO, List<Long> cids);

    void deleteBrandAndCategory(Long brandId);

    List<BrandDTO> findBrandByCategoryId(Long id);

    List<BrandDTO> findBrandListByBrandIds(List<Long> ids);

    BrandDTO findBrandByBrandId(Long id);
}
