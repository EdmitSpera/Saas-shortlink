package com.nageoffer.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
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
}
