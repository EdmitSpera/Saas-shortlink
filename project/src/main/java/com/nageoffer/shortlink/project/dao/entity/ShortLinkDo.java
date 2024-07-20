package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName("t_link")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShortLinkDo extends BaseDO {
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
     * 有效期类型
     */
    private int validDateType;


    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 图像
     */
    private String favicon;
}
