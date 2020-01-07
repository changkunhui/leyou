package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecParam;

import java.util.List;

/**
 * <p>
 * 规格参数组下的参数名 服务类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
public interface TbSpecParamService extends IService<TbSpecParam> {

    List<SpecParamDTO> findSpecParamByCidOrGid(Long gid, Long cid, Boolean searching);

    void saveParam(SpecParamDTO specParamDTO);

    void updateParam(SpecParamDTO specParamDTO);
}
