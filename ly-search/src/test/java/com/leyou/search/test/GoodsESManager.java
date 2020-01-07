package com.leyou.search.test;

import com.leyou.LySearchApplication;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author changkunhui
 * @date 2019/12/28 18:08
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class GoodsESManager {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;

    private ElasticsearchTemplate esTemplate;


    /**
     * 从数据库中查询数据,调用service中的方法写入elasticsearch中
     */
    @Test
    public void initGoodsIndex(){
        int page = 0;
        while (true){
            PageResult<SpuDTO> SpuByPage = itemClient.findGoodByPage(page, 100, null, true);
            if(SpuByPage == null || CollectionUtils.isEmpty(SpuByPage.getItems())){
                break;
            }
            //        把spu---->Goods
            List<SpuDTO> spuDTOList = SpuByPage.getItems();

            ArrayList<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : spuDTOList) {
                Goods goods = searchService.buildGoods(spuDTO);

                goodsList.add(goods);

            }


            //保存到es中
            goodsRepository.saveAll(goodsList);
            page++;
        }

//        elasticsearchTemplate.createIndex(Goods.class);
        //esTemplate.putMapping(Goods.class);
    }
}
