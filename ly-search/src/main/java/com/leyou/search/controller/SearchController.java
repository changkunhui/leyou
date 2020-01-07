package com.leyou.search.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.entity.Goods;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author changkunhui
 * @date 2019/12/28 21:39
 */

@RestController
public class SearchController {


    @Autowired
    private SearchService searchService;


    @PostMapping(value = "/page",name = "分页查询+关键字搜索")
    public ResponseEntity<PageResult<Goods>> findGoodsByPage(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> goodsPageResult = searchService.findGoodsByPage(searchRequest);
        return ResponseEntity.ok(goodsPageResult);
    }

    @PostMapping(value = "/filter",name = "商城查询过滤条件")
    public ResponseEntity<Map<String,List<?>>> findFilter(@RequestBody SearchRequest searchRequest){
        Map<String,List<?>> filterMap = searchService.findFilter(searchRequest);
        return ResponseEntity.ok(filterMap);
    }


}
