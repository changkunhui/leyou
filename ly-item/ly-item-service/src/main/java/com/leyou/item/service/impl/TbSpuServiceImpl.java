package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.entity.TbSpu;
import com.leyou.item.mapper.TbSpuMapper;
import com.leyou.item.service.TbSpuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
@Service
public class TbSpuServiceImpl extends ServiceImpl<TbSpuMapper, TbSpu> implements TbSpuService {

}
