package com.leyou.page.service;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author changkunhui
 * @date 2020/1/2 17:00
 */

@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 查询数据模型,填充到页面中的数据
     * @param spuId    spuId
     * @return
     */
    public Map<String,Object> loadItemData(Long spuId) {


        HashMap<String, Object> dataMap = new HashMap<>();

        //- spuName：应该 是spu表中的name属性
        SpuDTO spu = itemClient.findSpuBySpuId(spuId);
        dataMap.put("spuName",spu.getName());

        //subTitle：spu中 的副标题
        dataMap.put("subTitle",spu.getSubTitle());

        //brand：品牌对象
        BrandDTO brand = itemClient.findBrandByBrandId(spu.getBrandId());
        dataMap.put("brand",brand);

        //detail：商品详情SpuDetail
        SpuDetailDTO spuDetail = itemClient.findDetail(spuId);
        dataMap.put("detail",spuDetail);

        //categories：商品分类对象集合
        List<CategoryDTO> categoryList = itemClient.findCategoryListByCategoryIds(spu.getCategoryIds());
        dataMap.put("categories",categoryList);

        //skus：商品spu下的sku集合
        List<SkuDTO> skuList = itemClient.findSkuBySpuId(spuId);
        dataMap.put("skus",skuList);

        //specs：规格参数这个比较 特殊：
        List<SpecGroupDTO> specGroupWithParamList = itemClient.findSpecGroupWithParamListByCategoryId(spu.getCid3());
        dataMap.put("specs",specGroupWithParamList);

        return dataMap;
    }

    /**
     * 商品上架,创建静态页面
     * @param spuId
     */
    public void createItemPage(Long spuId) {


        Context context = new Context();
        context.setVariables(this.loadItemData(spuId));

        try(PrintWriter printWriter = new PrintWriter("/usr/local/var/www/item/"+spuId+".html")) {
            templateEngine.process("item",context,printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下架之后删除静态页面
     * @param spuId
     */
    public void removeItemPage(Long spuId) {
        File file = new File("/usr/local/var/www/item/" + spuId + ".html");
        file.delete();
    }
}
