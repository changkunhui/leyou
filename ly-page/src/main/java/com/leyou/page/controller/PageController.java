package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author changkunhui
 * @date 2020/1/2 16:42
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 跳转到商品详情页面
     * @param model     页面模型
     * @param spuId        spuId
     * @return          跳转的视图,前缀默认是classpath:templates/,后缀默认是:.html
     */
    @GetMapping("/item/{id}.html")
    public String toItemPage(Model model, @PathVariable(value = "id")Long spuId){
        //调用service查询数据,返回需要的数据
        Map<String,Object> map = pageService.loadItemData(spuId);
        //存入模型数据
        model.addAllAttributes(map);

        return "item";
    }
}
