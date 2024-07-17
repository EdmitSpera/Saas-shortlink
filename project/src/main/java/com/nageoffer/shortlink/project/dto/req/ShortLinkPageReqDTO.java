package com.nageoffer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDo;
import lombok.Data;

@Data
// 这里需要通过baseMapper查询，需要继承Mybatis-plus中的Page
public class ShortLinkPageReqDTO extends Page<ShortLinkDo> {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}
