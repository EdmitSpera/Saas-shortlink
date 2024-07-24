package com.nageoffer.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 错误界面控制器
 * RestController会将返回值返回成Json形式
 * Controller先从视图(前端页面)中去匹配
 */
@Controller
public class ShortLinkNotfoundController {

    @RequestMapping("/page/notfound")
    public String notfound(){
        return "notfound";
    }
}
