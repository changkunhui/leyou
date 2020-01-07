package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.mapper.TbSpecGroupMapper;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.security.acl.Group;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组 服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
@Service
public class TbSpecGroupServiceImpl extends ServiceImpl<TbSpecGroupMapper, TbSpecGroup> implements TbSpecGroupService {

    @Autowired
    private TbSpecParamService specParamService;

    /**
     * 根据分类id查询规格组
     * @param categoryId
     * @return
     */
    @Override
    public List<SpecGroupDTO> findSpecGroupByCategoryId(Long categoryId) {
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,categoryId);

        List<TbSpecGroup> specGroupList = this.list(queryWrapper);

        if(CollectionUtils.isEmpty(specGroupList)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(specGroupList, SpecGroupDTO.class);
    }

    /**
     * 新增规格分组信息
     * @param specGroupDTO
     */
    @Override
    public void saveGroup(SpecGroupDTO specGroupDTO) {
        TbSpecGroup tbSpecGroup = BeanHelper.copyProperties(specGroupDTO, TbSpecGroup.class);

        tbSpecGroup.setCreateTime(new Date());
        tbSpecGroup.setUpdateTime(new Date());

        //执行保存
        this.save(tbSpecGroup);
    }


    /**
     * 更改规格分组信息
     * @param specGroupDTO
     */
    @Override
    public void updateGroup(SpecGroupDTO specGroupDTO) {
        TbSpecGroup tbSpecGroup = BeanHelper.copyProperties(specGroupDTO, TbSpecGroup.class);

        tbSpecGroup.setUpdateTime(new Date());

        //执行修改
        this.updateById(tbSpecGroup);
    }


    @Autowired
    private TbSpecParamService paramService;
    /**
     * 删除规格参数分组,包括删除规格分组下面的参数
     * @param groupId
     */
    @Override
    @Transactional
    public void deleteGroupAndParams(Long groupId) {
        //先将分组下面的参数全部删除
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecParam::getGroupId,groupId);

        //进行删除
        boolean isDelete = paramService.remove(queryWrapper);
        if(!isDelete){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }

        //再将规格分组删除
        boolean isRemove = this.removeById(groupId);
        if(!isRemove){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }

    /**
     * 根据categoryId查询规格参数组和组内参数
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroupDTO> findSpecGroupWithParamListByCategoryId(Long cid) {

        List<SpecGroupDTO> specGroupList = this.findSpecGroupByCategoryId(cid);
        //查询出规格参数并且将规格参数封装到属性中
        /*
            可以实现,但是效率太低,故而不用
        for (SpecGroupDTO specGroupDTO : specGroupList) {
            List<SpecParamDTO> specParamList = specParamService.findSpecParamByCidOrGid(specGroupDTO.getId(), null, null);
            specGroupDTO.setParams(specParamList);
        }*/
        List<SpecParamDTO> specParamList = specParamService.findSpecParamByCidOrGid(null, cid, null);
        //根据组id把规格参数分组
        Map<Long, List<SpecParamDTO>> specParamMap = specParamList.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));

        specGroupList.stream().map(group->{
            group.setParams(specParamMap.get(group.getId()));
            return group;
        }).collect(Collectors.toList());

        return specGroupList;
    }


    /*private List<SpecGroupDTO> findSpecGroupByCategoryId(Long cid){
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,cid);

        List<TbSpecGroup> tbSpecGroupList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbSpecGroupList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(tbSpecGroupList,SpecGroupDTO.class);
    }*/


}
