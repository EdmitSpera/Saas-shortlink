package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.PathVariable;import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 * RestController相当于是ResponseBody和Controller的结合
 */
@RestController
@RequiredArgsConstructor    // 使用构造器的方式注入
public class UserController {

    private final UserService userService;
    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public UserRespDTO getUserByUsername(@PathVariable("username")String username){
        return userService.getUserByUsername(username);
    }

}
