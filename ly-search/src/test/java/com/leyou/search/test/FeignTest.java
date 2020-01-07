package com.leyou.search.test;

import com.leyou.LySearchApplication;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.BrandDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author changkunhui
 * @date 2019/12/28 16:12
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class FeignTest {


    @Autowired
    private ItemClient itemClient;

    @Test
    public void testFeign(){
        List<BrandDTO> brandDTOList = itemClient.findBrandByCategoryId(76l);

        for (BrandDTO brandDTO : brandDTOList) {
            System.err.println(brandDTO);
        }
    }



}
