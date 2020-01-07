package com.leyou.sms;

import org.junit.Test;


/**
 * @author changkunhui
 * @date 2020/1/7 13:28
 */
public class MianshiTest {


    @Test
    public void test01(){
        String num1 = new String("3") + new String("3");

        System.out.println(num1);
        num1.intern();
        System.out.println(num1);

        String num2 = "33";
        System.out.println(num1 == num2);



    }




}


