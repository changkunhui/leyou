package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * item微服务提供的远程调用的feign接口
 * @author changkunhui
 * @date 2019/12/28 16:09
 */
@Component
@FeignClient("item-service")
public interface ItemClient {

    @GetMapping(value = "/brand/of/category",name = "根据categoryId查询品牌数据")
    List<BrandDTO> findBrandByCategoryId(@RequestParam("id") Long id);

    @GetMapping(value = "/spu/page",name = "分页查询商品SPU信息")
    PageResult<SpuDTO> findGoodByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable
    );

    @GetMapping(value = "/sku/of/spu",name = "根据spuId查询sku")
    List<SkuDTO> findSkuBySpuId(@RequestParam("id") Long spuId);

    @GetMapping(value = "/spu/detail",name = "根据spuId获取spuDetail的数据")
    SpuDetailDTO findDetail(@RequestParam("id") Long spuId);

    @GetMapping("/spec/params")
    List<SpecParamDTO> findSpecParamByCidOrGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    );

    @GetMapping(value = "/brand/list",name = "根据品牌Id集合批量查询品牌数据")
    List<BrandDTO> findBrandListByBrandIds(@RequestParam("ids") List<Long> ids);

    @GetMapping(value = "/category/list",name = "根据分类id集合获取分类集合")
    List<CategoryDTO> findCategoryListByCategoryIds(@RequestParam(value = "ids") List<Long> ids);

    @GetMapping(value = "/spu/{id}",name = "根据spuId查询spu对象")
    SpuDTO findSpuBySpuId(@PathVariable(value = "id") Long id);

    @GetMapping(value = "/brand/{id}",name = "根据品牌Id查询品牌数据")
    BrandDTO findBrandByBrandId(@PathVariable("id") Long id);

    @GetMapping(value = "/spec/of/category",name = "根据categoryId查询规格参数组和组内参数")
    List<SpecGroupDTO> findSpecGroupWithParamListByCategoryId(@RequestParam("id") Long id);

    @GetMapping(value = "/sku/list",name = "根据skuIds查询sku集合信息")
    List<SkuDTO> findSkuListByIds(@RequestParam("ids") List<Long> ids);

    @PutMapping(value = "/stock/minus",name = "减库存")
    void stockMinus(@RequestBody Map<Long,Integer> skuIdAndNumMap);

    @PutMapping(value = "/stock/plus",name = "恢复库存")
    Void stockPlus(@RequestBody Map<Long,Integer> skuIdAndNumMap);
}
