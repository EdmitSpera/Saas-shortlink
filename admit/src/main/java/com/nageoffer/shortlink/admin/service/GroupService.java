package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDo;

/**
 * 短链接分组接口
 */
public interface GroupService extends IService<GroupDo> {

    /**
     * 新增短链接分组
     * @param groupName
     */
    void saveGroup(String groupName);
}
