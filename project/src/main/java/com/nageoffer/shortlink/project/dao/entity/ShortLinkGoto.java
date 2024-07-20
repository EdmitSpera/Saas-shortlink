package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("t_link_goto")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGoto {
    /**
     * 主键id
     */
    private long id;

    /**
     * 分组id
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
