package com.nageoffer.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 短链接分组实体
 */
@TableName("t_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupDo extends BaseDatabaseDo{
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;

}
