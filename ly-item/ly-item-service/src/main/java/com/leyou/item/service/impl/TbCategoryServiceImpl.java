package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryBrandService;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author SYL
 * @since 2019-12-24
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Autowired
    private TbCategoryBrandService categoryBrandService;

    /**
     * 根据品牌Id查询分类信息
     * @param brandId
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryListByBrandId(Long brandId) {

        List<TbCategory> categoryList =  this.getBaseMapper().selectCategoryByBrandId(brandId);

        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(categoryList,CategoryDTO.class);
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        TbCategory tbCategory = BeanHelper.copyProperties(categoryDTO, TbCategory.class);

        tbCategory.setCreateTime(new Date());
        tbCategory.setUpdateTime(new Date());

        //执行保存
        boolean isInsert = this.save(tbCategory);

        if(!isInsert){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    /**
     * 更新分类信息
     * @param categoryDTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        TbCategory tbCategory = BeanHelper.copyProperties(categoryDTO, TbCategory.class);

        tbCategory.setUpdateTime(new Date());

        //执行更新
        boolean isUpdate = this.updateById(tbCategory);

        if(!isUpdate){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    /**
     * 删除分类信息,以及将中间表有关该分类的数据删除
     * @param cid
     */
    @Override
    @Transactional
    public void deleteCategoryAndBrand(String cid) {

        //先删除中间表数据
        QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbCategoryBrand::getCategoryId,cid);

        if(!CollectionUtils.isEmpty(categoryBrandService.list(queryWrapper))){
            boolean isRemove = categoryBrandService.remove(queryWrapper);
            if(!isRemove){
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }

        //再删除分类信息
        boolean isDelete = this.removeById(cid);
        if(!isDelete){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }



    }


    /**
     * 根据分类id集合获取分类集合
     * @param ids
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryListByCategoryIds(List<Long> ids) {

        Collection<TbCategory> categoryCollection = this.listByIds(ids);
        if(CollectionUtils.isEmpty(categoryCollection)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        //把tbCategory的集合转为CategoryDTO的集合
        List<CategoryDTO> categoryDTOList = categoryCollection.stream().map(tbCategory -> {
            return BeanHelper.copyProperties(tbCategory,CategoryDTO.class);
        }).collect(Collectors.toList());

        return categoryDTOList;
    }
}
