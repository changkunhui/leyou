package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * @author changkunhui
 * @date 2019/12/27 13:22
 */

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbSpuService spuService;
    @Autowired
    private TbCategoryService categoryService;
    @Autowired
    private TbBrandService brandService;
    @Autowired
    private TbSpuDetailService spuDetailService;
    @Autowired
    private TbSkuService skuService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 分页查询商品SPU信息
     *
     * @param page     当前页
     * @param rows     每页显示条数
     * @param key      搜索关键字
     * @param saleable 是否下架
     * @return
     */
    @Override
    public PageResult<SpuDTO> findGoodByPage(Integer page, Integer rows, String key, Boolean saleable) {

        Page p = new Page(page, rows);//分页

        //创建构造查询条件的对象
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();

        //构建搜索查询条件
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.lambda().like(TbSpu::getName, key);
        }

        //构建是否上下架的条件
        if (saleable != null){
            queryWrapper.lambda().eq(TbSpu::getSaleable,saleable);
        }

        //执行查询
        IPage iPage = spuService.page(p, queryWrapper);
        if(iPage == null && CollectionUtils.isEmpty(iPage.getRecords())){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //数据总量
        long total = iPage.getTotal();

        //转Bean
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(iPage.getRecords(), SpuDTO.class);

        //处理品牌名称和分类名称
        handleBrandAndCategoryName(spuDTOList);

        return new PageResult<SpuDTO>(total,spuDTOList);
    }

    /**
     * 保存spu和sku和spuDetail数据
     * @param spuDTO
     */
    @Override
    @Transactional
    public void saveGoods(SpuDTO spuDTO) {

        //保存spu
        //转bean
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean isInsertSpu = spuService.save(tbSpu);
        if(!isInsertSpu){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        Long spuId = tbSpu.getId();

        //保存tbSpuDetail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        tbSpuDetail.setSpuId(spuId);
        boolean isInsertDetail= spuDetailService.save(tbSpuDetail);
        if(!isInsertDetail){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }


        //保存sku信息
        List<SkuDTO> spuDTOSkuList = spuDTO.getSkus();
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(spuDTOSkuList, TbSku.class);
        tbSkuList = tbSkuList.stream().map(sku ->{
            sku.setSpuId(spuId);
            return sku;
        }).collect(Collectors.toList());
        boolean isInsertSku = skuService.saveBatch(tbSkuList);
        if(!isInsertSku){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    /**
     * 修改商品上下架,同时记得修改sku的上下架信息
     * @param id
     * @param saleable
     */
    @Override
    @Transactional
    public void updateSaleable(Long id, Boolean saleable) {
        //先将所有的spu下架
        TbSpu tbSpu = new TbSpu();
        tbSpu.setId(id);
        tbSpu.setSaleable(saleable);
        boolean isUpdate = spuService.updateById(tbSpu);
        if(!isUpdate){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //将对应的所有的sku下架
        UpdateWrapper<TbSku> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TbSku::getSpuId,id);
        updateWrapper.lambda().set(TbSku::getEnable,saleable);

        boolean isUpdateSku = skuService.update(updateWrapper);
        if(!isUpdateSku){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //发送消息,消息的内容是spuId
        String tag = saleable ? ITEM_UP_TAGS : ITEM_DOWN_TAGS;//上下架对应的tag
        rocketMQTemplate.convertAndSend(ITEM_TOPIC_NAME +":"+tag,id);
    }


    /**
     * 根据spuId查询SpuDetail的数据
     * @param spuId
     * @return
     */
    @Override
    public SpuDetailDTO findDetail(Long spuId) {
        TbSpuDetail tbSpuDetail = spuDetailService.getById(spuId);
        if(tbSpuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpuDetail,SpuDetailDTO.class);
    }


    /**
     * 根据spuId查询sku数据
     * @param spuId
     * @return
     */
    @Override
    public List<SkuDTO> findSkuBySpuId(Long spuId) {
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        List<TbSku> tbSkuList = skuService.list(queryWrapper);
        return BeanHelper.copyWithCollection(tbSkuList,SkuDTO.class);
    }

    /**
     * 更新商品信息
     * @param spuDTO
     */
    @Override
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        //更新spu
        //转bean
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        tbSpu.setUpdateTime(new Date());
        boolean isUpdateSpu = spuService.updateById(tbSpu);
        if(!isUpdateSpu){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        Long spuId = tbSpu.getId();

        //修改tbSpuDetail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        tbSpuDetail.setSpuId(spuId);
        tbSpuDetail.setUpdateTime(new Date());
        boolean isUpdateDetail= spuDetailService.updateById(tbSpuDetail);
        if(!isUpdateDetail){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

        //删除之前的sku信息
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        boolean isDelete = skuService.remove(queryWrapper);
        if(!isDelete){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }


        //重新保存sku信息
        List<SkuDTO> spuDTOSkuList = spuDTO.getSkus();
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(spuDTOSkuList, TbSku.class);
        tbSkuList = tbSkuList.stream().map(sku ->{
            sku.setSpuId(spuId);
            return sku;
        }).collect(Collectors.toList());
        boolean isInsertSku = skuService.saveBatch(tbSkuList);
        if(!isInsertSku){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据spuId查询spu对象
     * @param id
     * @return
     */
    @Override
    public SpuDTO findSpuBySpuId(Long id) {
        TbSpu tbSpu = spuService.getById(id);
        if(tbSpu == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpu,SpuDTO.class);
    }


    /**
     * 处理品牌名称和分类名称
     * @param spuDTOList
     */
    private void handleBrandAndCategoryName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) {
            //分类信息
            List<Long> categoryIds = spuDTO.getCategoryIds();

            Collection<TbCategory> categoryIdList = categoryService.listByIds(categoryIds);
            if(CollectionUtils.isEmpty(categoryIdList)){
                throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
            }

            String categoryNames = categoryIdList.stream().map(TbCategory::getName).collect(Collectors.joining("/"));

            spuDTO.setCategoryName(categoryNames);

            //品牌信息
            TbBrand tbBrand = brandService.getById(spuDTO.getBrandId());
            if(tbBrand == null){
                throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
            }
            spuDTO.setBrandName(tbBrand.getName());
        }
    }
}
