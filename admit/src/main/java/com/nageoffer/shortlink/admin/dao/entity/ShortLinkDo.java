package com.nageoffer.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName("t_link")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShortLinkDo extends BaseDatabaseDo {
    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;


    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

}
