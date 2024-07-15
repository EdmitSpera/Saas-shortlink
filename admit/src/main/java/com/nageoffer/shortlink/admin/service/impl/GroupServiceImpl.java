package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.dao.entity.GroupDo;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.util.RandomSequenceGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        // 生成唯一的Gid
        do {
            gid = RandomSequenceGenerator.generateRandomSequence();
        } while (hasGid(gid));
        GroupDo groupDo = GroupDo.builder()
                .gid(gid)
                .name(groupName)
                .build();
        baseMapper.insert(groupDo);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        // TODO 获取用户名
        LambdaQueryWrapper<GroupDo> queryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getDelFlag, 0)
                .eq(GroupDo::getUsername, "Lambert")
                .orderByDesc(GroupDo::getSortOrder, GroupDo::getUpdateTime);
        List<GroupDo> groupDoList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDoList, ShortLinkGroupRespDTO.class);
    }

    /**
     * 判断数据库中是否存在随机生成的Gid
     * @return true是存在
     */
    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDo> queryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getGid, gid)
                // TODO 设置用户名 你的用户名是通过网关传输过来并进行解析，不能通过用户来传这个username，可能会有盗接口刷的风险
                .eq(GroupDo::getUsername, null);
        GroupDo hasGroupFlag = baseMapper.selectOne(queryWrapper);
        return hasGroupFlag != null;
    }
}
