package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.mapper.TbSpecParamMapper;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 规格参数组下的参数名 服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
@Service
public class TbSpecParamServiceImpl extends ServiceImpl<TbSpecParamMapper, TbSpecParam> implements TbSpecParamService {

    /**
     * 根据前端传入的参数不同进行查询
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @Override
    public List<SpecParamDTO> findSpecParamByCidOrGid(Long gid, Long cid, Boolean searching) {

        //cid和gid不能同时为null,至少要传递一个
        if(gid == null && cid == null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        //构建查询对象
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();

        //根据传入的参数构建查询条件
        if(gid != null){
            queryWrapper.lambda().eq(TbSpecParam::getGroupId,gid);
        }

        if(cid != null){
            queryWrapper.lambda().eq(TbSpecParam::getCid,cid);
        }

        if(searching != null){
            queryWrapper.lambda().eq(TbSpecParam::getSearching,searching);
        }

        //执行查询
        List<TbSpecParam> specParamList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(specParamList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(specParamList,SpecParamDTO.class);
    }

    /**
     * 新增规格参数
     * @param specParamDTO
     */
    @Override
    public void saveParam(SpecParamDTO specParamDTO) {
        TbSpecParam tbSpecParam = BeanHelper.copyProperties(specParamDTO, TbSpecParam.class);

        tbSpecParam.setCreateTime(new Date());
        tbSpecParam.setUpdateTime(new Date());

        //执行插入
        boolean isInsert = this.save(tbSpecParam);
        if(!isInsert){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 修改规格参数信息
     * @param specParamDTO
     */
    @Override
    public void updateParam(SpecParamDTO specParamDTO) {
        TbSpecParam tbSpecParam = BeanHelper.copyProperties(specParamDTO, TbSpecParam.class);

        tbSpecParam.setUpdateTime(new Date());

        //执行更新
        boolean isInsert = this.updateById(tbSpecParam);
        if(!isInsert){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
