package com.nageoffer.shortlink.admin.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 用户返回参数相应实体
 * 注意不要暴露相关用户敏感信息
 */
@Data
public class UserRespDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;


}
