package com.nageoffer.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 * RestController相当于是ResponseBody和Controller的结合
 */
@RestController
@RequiredArgsConstructor    // 使用构造器的方式注入
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息（脱敏）
     *
     * @param username 用户名
     * @return 用户信息结果
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询未脱敏的用户信息
     *
     * @param username 用户名
     * @return 用户实际信息结果
     */
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }

    /**
     * 根据用户名查询是否存在
     *
     * @param username 用户名
     * @return 用户名存在与否结果
     */
    @GetMapping("/api/shortlink/v1/has-username/user/{username}")
    public Result<Boolean> hasUserName(@PathVariable("username") String username) {
        return Results.success(!userService.hasUsername(username));
    }

    /**
     * 用户注册接口
     *
     * @param requestParam 注册请求参数
     * @return 空结果
     */
    @PostMapping("/api/shortlink/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.Register(requestParam);
        return Results.success();
    }

    /**
     * 用户信息更新接口
     *
     * @param userUpdateReqDTO 更新请求参数
     * @return 空结果
     */
    @PutMapping("/api/shortlink/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        userService.update(userUpdateReqDTO);
        return Results.success();
    }

    /**
     * 用户登录接口
     *
     * @param requestParam 登录请求参数
     * @return 登录响应结果
     */
    @PostMapping("/api/shortlink/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户登录状态
     *
     * @param username 用户名
     * @param token    用户令牌
     * @return 登录状态结果
     */
    @GetMapping("/api/shortlink/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLoginStatus(username, token));
    }

    @DeleteMapping("/api/shortlink/v1/user")
    public Result<Boolean> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        Boolean isLogout = userService.logout(username, token);
        return Results.success(isLogout);
    }

}
