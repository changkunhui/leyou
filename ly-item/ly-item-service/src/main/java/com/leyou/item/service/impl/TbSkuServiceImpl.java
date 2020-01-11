package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.entity.TbSku;
import com.leyou.item.mapper.TbSkuMapper;
import com.leyou.item.service.TbSkuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
@Service
public class TbSkuServiceImpl extends ServiceImpl<TbSkuMapper, TbSku> implements TbSkuService {

    /**
     * 减库存
     * @param skuId
     * @param num   减少的数量
     */
    @Override
    public void stockMinus(Long skuId, Integer num) {
        this.getBaseMapper().stockMinus(skuId,num);
    }

    @Override
    public void stockPlus(Long skuId, Integer num) {
        this.getBaseMapper().stockPlus(skuId,num);
    }
}
