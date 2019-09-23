package com.weui.krpano.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @content:
 * @auther: 左飞
 * @date: 2019/9/21 14:29
 */
@Controller
public class IndexController {

    @GetMapping("/krapno")
    public String krapno(){
        return "krapno.html";
    }

    @GetMapping("/upload")
    public String upload(){
        return "upload.html";
    }
}
