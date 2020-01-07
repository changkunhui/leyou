package com.leyou.item.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.leyou.item.service.TbCategoryBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-24
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {

    @Autowired
    private TbCategoryBrandService categoryBrandService;


    /**
     * 分页查询品牌
     * @param key 前段搜索的关键字,非必填
     * @param page  当前页
     * @param rows  每页显示的条数
     * @param sortBy   排序的字段    非必填
     * @param desc     排序的规则    非必填
     * @return
     */
    @Override
    public PageResult<BrandDTO> findPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //构建分页
        Page<TbBrand> p = new Page<TbBrand>(page,rows);
        //构建查询条件
        QueryWrapper<TbBrand> queryWrapper = new QueryWrapper<>();

        //判断是否传递了关键字
        if(StringUtils.isNotBlank(key)){
            //构建关键字的查询条件
            queryWrapper.lambda().like(TbBrand::getName,key).or().like(TbBrand::getLetter,key);
        }

        //判断是否传递了排序字段
        if(StringUtils.isNotBlank(sortBy)){
            //判断排序规则
            if(desc){
                p.setDesc(sortBy);
            }else{
                p.setAsc(sortBy);
            }
        }

        //执行查询
        IPage<TbBrand> iBrandPage = this.page(p, queryWrapper);

        //获取当前页数据
        List<TbBrand> brandList = iBrandPage.getRecords();

        //判断结果
        if(CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //总条数
        long total = iBrandPage.getTotal();

        //拷贝属性
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(brandList, BrandDTO.class);

        return new PageResult<BrandDTO>(total,brandDTOList);
    }


    /**
     * 保存品牌以及和商品中间表的数据
     * @param brandDTO  前端传过来的品牌的DTO对象
     * @param cids    对应的商品id
     */
    @Override
    @Transactional
    public void saveBrandAndCategory(BrandDTO brandDTO, List<Long> cids) {
        //先保存品牌
        //转化为Brand对象
        TbBrand tbBrand = BeanHelper.copyProperties(brandDTO, TbBrand.class);
        tbBrand.setCreateTime(new Date());
        tbBrand.setUpdateTime(new Date());
        //保存品牌
        boolean isInsert = this.save(tbBrand);

        if(!isInsert){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        Long id = tbBrand.getId();
        TbCategoryBrand categoryBrand = null;
        for (Long cid : cids) {
            categoryBrand = new TbCategoryBrand();
            categoryBrand.setBrandId(id);
            categoryBrand.setCategoryId(cid);
            //categoryBrand.insert();
            categoryBrandService.save(categoryBrand);
        }
    }

    /**
     * 修改品牌以及和商品中间表的数据
     * @param brandDTO 前端传过来的品牌的DTO对象
     * @param cids 对应的商品id
     */
    @Override
    @Transactional
    public void updateBrandAndCategory(BrandDTO brandDTO, List<Long> cids) {
        //先更新品牌信息
        TbBrand tbBrand = BeanHelper.copyProperties(brandDTO, TbBrand.class);
        tbBrand.setUpdateTime(new Date());
        //执行更新
        boolean isUpdate = this.updateById(tbBrand);
        if(!isUpdate){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //更改中间表数据
        Long brandId = tbBrand.getId();
        if(!CollectionUtils.isEmpty(cids)) {
            //先删除品牌之前的工作分类信息
            QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TbCategoryBrand::getBrandId,brandId);
            boolean isDelete = categoryBrandService.remove(queryWrapper);

            //判断是否成功
            if(!isDelete){
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }

            //添加新的分类信息
            ArrayList<TbCategoryBrand> list = new ArrayList<>();
            TbCategoryBrand categoryBrand = null;
            for (Long cid : cids) {
                categoryBrand  = new TbCategoryBrand();
                categoryBrand.setCategoryId(cid);
                categoryBrand.setBrandId(brandId);
                list.add(categoryBrand);
            }

            //执行插入操作
            categoryBrandService.saveBatch(list);

        }
    }

    /**
     * 根据brandId删除品牌以及中间表的数据
     * @param brandId
     */
    @Override
    @Transactional
    public void deleteBrandAndCategory(Long brandId) {
        //先删除中间表的数据
        QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbCategoryBrand::getBrandId,brandId);

        if(!CollectionUtils.isEmpty(categoryBrandService.list(queryWrapper))){//不等于空再删除
            boolean isRemove = categoryBrandService.remove(queryWrapper);
            if(!isRemove){
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }

        //再删除品牌表数据
        boolean isDelete = this.removeById(brandId);
        if(!isDelete){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }

    /**
     * 根据分类id查询品牌数据
     * @param id
     * @return
     */
    @Override
    public List<BrandDTO> findBrandByCategoryId(Long id) {
        List<TbBrand> brandList = this.getBaseMapper().findBrandByCategoryId(id);
        if(CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(brandList,BrandDTO.class);
    }


    /**
     * 根基品牌Id的集合批量查询品牌数据
     * @param ids
     * @return
     */
    @Override
    public List<BrandDTO> findBrandListByBrandIds(List<Long> ids) {
        Collection<TbBrand> brandCollection = this.listByIds(ids);
        if(CollectionUtils.isEmpty(brandCollection)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        List<BrandDTO> brandDTOList = brandCollection.stream().map(tbBrand -> {
            return BeanHelper.copyProperties(tbBrand,BrandDTO.class);
        }).collect(Collectors.toList());

        return brandDTOList;
    }

    /**
     * 根据品牌id查询品牌数据
     * @param id
     * @return
     */
    @Override
    public BrandDTO findBrandByBrandId(Long id) {
        TbBrand tbBrand = this.getById(id);
        if(tbBrand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbBrand,BrandDTO.class);
    }
}
