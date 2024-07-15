package com.nageoffer.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDo> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户实体类
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    Boolean hasUsername(String username);

    /**
     * 注册接口
     * @param requestParam
     */
    void Register(UserRegisterReqDTO requestParam);

    /**
     * 修改接口
     * @param requestParam
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     *
     * @param requestParam
     * @return token登录令牌
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLoginStatus(String username,String token);
}
