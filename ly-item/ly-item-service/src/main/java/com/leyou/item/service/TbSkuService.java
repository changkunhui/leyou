package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.entity.TbSku;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbSkuService extends IService<TbSku> {

    void stockMinus(Long skuId, Integer num);

    void stockPlus(Long skuId, Integer num);
}
