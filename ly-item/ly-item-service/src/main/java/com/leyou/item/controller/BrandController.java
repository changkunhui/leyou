package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author changkunhui
 * @date 2019/12/24 18:04
 */

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private TbBrandService brandService;


    @GetMapping(value = "/page",name = "分页查询品牌")
    public ResponseEntity<PageResult<BrandDTO>> findPage(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc
    ){
        //逻辑交给service去实现
        PageResult<BrandDTO> pageResult = brandService.findPage(key,page,rows,sortBy,desc);
        return ResponseEntity.ok(pageResult);
    }

    @PostMapping(name="保存品牌信息")
    public ResponseEntity<Void> save(BrandDTO brandDTO, @RequestParam(value = "cids") List<Long> cids){
        //调用service层保存品牌和中间表
        brandService.saveBrandAndCategory(brandDTO,cids);
        return ResponseEntity.ok().build();
    }

    @PutMapping(name="修改品牌信息")
    public ResponseEntity<Void> update(BrandDTO brandDTO, @RequestParam(value = "cids") List<Long> cids){
        //调用service层保存品牌和中间表
        brandService.updateBrandAndCategory(brandDTO,cids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(name="删除品牌以及中间表的数据")
    public ResponseEntity<Void> delete(@RequestParam(value = "id") Long brandId){
        brandService.deleteBrandAndCategory(brandId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/of/category",name = "根据categoryId查询品牌数据")
    public ResponseEntity<List<BrandDTO>> findBrandByCategoryId(@RequestParam("id") Long id){
        List<BrandDTO> brandDTOList = brandService.findBrandByCategoryId(id);
        return ResponseEntity.ok(brandDTOList);
    }

    @GetMapping(value = "/list",name = "根据品牌Id集合批量查询品牌数据")
    public ResponseEntity<List<BrandDTO>> findBrandListByBrandIds(@RequestParam("ids") List<Long> ids){
        List<BrandDTO> brandDTOList = brandService.findBrandListByBrandIds(ids);
        return ResponseEntity.ok(brandDTOList);
    }


    @GetMapping(value = "/{id}",name = "根据品牌Id查询品牌数据")
    public ResponseEntity<BrandDTO> findBrandByBrandId(@PathVariable("id") Long id){
        BrandDTO brandDTO = brandService.findBrandByBrandId(id);
        return ResponseEntity.ok(brandDTO);
    }





}
