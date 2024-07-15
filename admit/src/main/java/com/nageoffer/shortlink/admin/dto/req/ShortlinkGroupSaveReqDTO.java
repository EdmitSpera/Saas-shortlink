package com.nageoffer.shortlink.admin.dto.req;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 短链接分组创建参数
 */
@Data
public class ShortlinkGroupSaveReqDTO {
    @JsonProperty("groupName")
    private String name;
}
