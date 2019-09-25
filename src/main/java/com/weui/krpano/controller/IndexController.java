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

    @GetMapping("/krpano")
    public String krpano(){
        return "krpano.html";
    }

    @GetMapping("/upload")
    public String upload(){
        return "upload.html";
    }

    @GetMapping("/home")
    public String home(){
        return "home.html";
    }
}
