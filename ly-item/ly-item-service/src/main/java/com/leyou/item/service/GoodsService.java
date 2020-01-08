package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;

import java.util.List;


/**
 * @author changkunhui
 * @date 2019/12/27 13:21
 */
public interface GoodsService {
    PageResult<SpuDTO> findGoodByPage(Integer page, Integer rows, String key, Boolean saleable);

    void saveGoods(SpuDTO spuDTO);

    void updateSaleable(Long id, Boolean saleable);

    SpuDetailDTO findDetail(Long spuId);

    List<SkuDTO> findSkuBySpuId(Long spuId);

    void updateGoods(SpuDTO spuDTO);

    SpuDTO findSpuBySpuId(Long id);

    List<SkuDTO> findSkuListByIds(List<Long> ids);
}
