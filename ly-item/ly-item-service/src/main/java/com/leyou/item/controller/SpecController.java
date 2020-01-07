package com.leyou.item.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author changkunhui
 * @date 2019/12/25 20:55
 */

@RestController
@RequestMapping(value = "/spec",name = "规格组和规格参数管理")
public class SpecController {

    @Autowired
    private TbSpecGroupService specGroupService;

    @Autowired
    private TbSpecParamService specParamService;


    @GetMapping(value = "/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupByCategoryId(@RequestParam("id") Long categoryId){
        List<SpecGroupDTO> groupDTOList = specGroupService.findSpecGroupByCategoryId(categoryId);
        return ResponseEntity.ok(groupDTOList);
    }


    /**
     * 根据不同的参数查询规格参数
     * @param gid   分组id,不是必须的
     * @param cid   分类id,不是必须的
     * @param searching     是否搜索,不是必须的
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParamByCidOrGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    ){
        List<SpecParamDTO> paramDTOList =  specParamService.findSpecParamByCidOrGid(gid,cid,searching);
        return ResponseEntity.ok(paramDTOList);
    }


    @PostMapping(value = "/group",name = "新增规格分组信息")
    public ResponseEntity<Void> saveGroup(@RequestBody SpecGroupDTO specGroupDTO){
        specGroupService.saveGroup(specGroupDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/group",name = "修改规格分组信息")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroupDTO specGroupDTO){
        specGroupService.updateGroup(specGroupDTO);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/param",name = "新增规格参数信息")
    public ResponseEntity<Void> saveParam(@RequestBody SpecParamDTO specParamDTO){
        specParamService.saveParam(specParamDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/param",name = "修改规格参数信息")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParamDTO specParamDTO){
        specParamService.updateParam(specParamDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/param/{id}",name = "删除规格参数信息")
    public ResponseEntity<Void> deleteParam(@PathVariable("id") Long id){
        boolean isDelete = specParamService.removeById(id);
        if(!isDelete){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/group/{id}",name = "删除规格分组信息")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id){
        specGroupService.deleteGroupAndParams(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/of/category",name = "根据categoryId查询规格参数组和组内参数")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupWithParamListByCategoryId(@RequestParam("id") Long id){
        List<SpecGroupDTO> specGroupDTOList = specGroupService.findSpecGroupWithParamListByCategoryId(id);
        return ResponseEntity.ok(specGroupDTOList);
    }



}
