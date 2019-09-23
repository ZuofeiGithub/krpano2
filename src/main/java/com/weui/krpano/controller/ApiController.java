package com.weui.krpano.controller;

import com.weui.krpano.utils.KrpanoUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @content:
 * @auther: 左飞
 * @date: 2019/9/23 9:12
 */
@RestController
public class ApiController {

    @GetMapping("uploadImage")
    public void uploadImage(){
        KrpanoUtil.getInstance().createKrapano("1.19").createPreView(null,"snow.jpg",null,null).generateXml().docToString();
    }
}
