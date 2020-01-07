package com.leyou.item.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.service.TbCategoryService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author changkunhui
 * @date 2019/12/24 16:30
 */

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private TbCategoryService categoryService;


    @RequestMapping(name = "新增分类")
    public ResponseEntity<Void> save(@RequestBody CategoryDTO categoryDTO){

        categoryService.saveCategory(categoryDTO);

        return ResponseEntity.ok().build();
    }

    @PutMapping(name = "更新分类信息")
    public ResponseEntity<Void> update(@RequestBody CategoryDTO categoryDTO){

        categoryService.updateCategory(categoryDTO);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(name = "删除分类信息")
    public ResponseEntity<Void> delete(@RequestParam(value = "id") String id){

        categoryService.deleteCategoryAndBrand(id);

        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/of/parent",name = "根据parentId查询分类信息")
    //@CrossOrigin(origins="http://manage.leyou.com")
    public ResponseEntity<List<CategoryDTO>> findByParentId(@RequestParam("pid")Long pid){
        //创建一个用来构建查询条件的对象
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        //使用lambda构建查询条件
        queryWrapper.lambda().eq(TbCategory::getParentId,pid);
        //执行查询
        List<TbCategory> categoryList = categoryService.list(queryWrapper);

        //使用工具类进行对象转换
        List<CategoryDTO> categoryDTOList = BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);

        return ResponseEntity.ok(categoryDTOList);
    }

    @GetMapping(value = "/of/brand",name = "根据品牌Id查询分类信息")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByBrandId(@RequestParam(value = "id") Long brandId){
        return ResponseEntity.ok(categoryService.findCategoryListByBrandId(brandId));
    }


    @GetMapping(value = "/list",name = "根据分类id集合获取分类集合")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByCategoryIds(@RequestParam(value = "ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.findCategoryListByCategoryIds(ids));
    }
 }
