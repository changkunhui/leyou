package com.leyou.page.test;

import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @author changkunhui
 * @date 2020/1/2 19:07
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class PageTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PageService pageService;

    @Autowired
    private ItemClient itemClient;


    @Test
    public void createPage(){

        Context context = new Context();
        Map map = pageService.loadItemData(106L);
        context.setVariables(map);
        //  D:\class116-leyou\nginx-1.16.0\html\item
        try(PrintWriter printWriter = new PrintWriter("/usr/local/var/www/item/106.html")) {
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void createAllPage(){
//        - Context：运行上下文 存储的是数据
//         - TemplateResolver：模板解析器 模板文件
//         - TemplateEngine：模板引擎 通过模板引擎可以把 数据结婚模板生成静态页面
        int page =1;
        while (true){
            PageResult<SpuDTO> spuPage = itemClient.findGoodByPage(page, 50, null, true);
            if(spuPage==null||spuPage.getItems().size()==0){
                break;
            }
            List<SpuDTO> items = spuPage.getItems();
            for (SpuDTO item : items) {
                Context context = new Context();
                Map map = pageService.loadItemData(item.getId());
                context.setVariables(map);
                try(PrintWriter printWriter = new PrintWriter("/usr/local/var/www/item/"+item.getId()+".html")) {
                    templateEngine.process("item",context,printWriter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            page++;

        }



    }

}
