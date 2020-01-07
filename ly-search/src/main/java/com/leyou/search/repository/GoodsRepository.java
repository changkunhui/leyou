package com.leyou.search.repository;

import com.leyou.search.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author changkunhui
 * @date 2019/12/28 18:06
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
