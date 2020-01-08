package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品相关的spu,sku,spuDetail公用一个controller
 * @author changkunhui
 * @date 2019/12/27 13:13
 */

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    @GetMapping(value = "/spu/page",name = "分页查询商品SPU信息")
    public ResponseEntity<PageResult<SpuDTO>> findGoodByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable
    ){
        PageResult<SpuDTO> pageResult = goodsService.findGoodByPage(page,rows,key,saleable);
        return ResponseEntity.ok(pageResult);
    }


    @PostMapping(name = "保存spu和sku信息")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.ok().build();
    }


    @PutMapping(name = "修改spu和sku信息")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.ok().build();
    }


    @PutMapping(value = "/spu/saleable",name = "修改商品上下架")
    public ResponseEntity<Void> updateSaleable(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "saleable") Boolean saleable
    ){
        goodsService.updateSaleable(id,saleable);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/spu/detail",name = "获取spuDetail的数据")
    public ResponseEntity<SpuDetailDTO> findDetail(@RequestParam("id") Long spuId){
        SpuDetailDTO spuDetailDTO= goodsService.findDetail(spuId);
        return ResponseEntity.ok(spuDetailDTO);
    }

    @GetMapping(value = "/sku/of/spu",name = "根据spuId查询sku")
    public ResponseEntity<List<SkuDTO>> findSkuBySpuId(@RequestParam("id") Long spuId){
        List<SkuDTO> skuDTOList = goodsService.findSkuBySpuId(spuId);
        return ResponseEntity.ok(skuDTOList);
    }

    @GetMapping(value = "/spu/{id}",name = "根据spuId查询spu")
    public ResponseEntity<SpuDTO> findSpuBySpuId(@PathVariable(value = "id") Long id){
        SpuDTO SpuDTO = goodsService.findSpuBySpuId(id);
        return ResponseEntity.ok(SpuDTO);
    }


    @GetMapping(value = "/sku/list",name = "根据skuIds查询sku集合信息")
    public ResponseEntity<List<SkuDTO>> findSkuListByIds(@RequestParam("ids") List<Long> ids){
        List<SkuDTO> skuDTOList = goodsService.findSkuListByIds(ids);
        return ResponseEntity.ok(skuDTOList);
    }

}
