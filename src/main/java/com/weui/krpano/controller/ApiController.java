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
        KrpanoUtil.getInstance().createKrpano("1.91",null)
                .addRootPreview(null,"/images/snow.jpg",null,null)
                .addRootSphereImage("/images/snow.jpg")
                .build()
                .generateXml();
        KrpanoUtil.getInstance().createKrpano("1.91",null)
                .addScene("demo",null,false,"")
                .addScenePreview(null,"/images/sun.jpg",null,null)
                .build()
                .generateXml();
    }
}
