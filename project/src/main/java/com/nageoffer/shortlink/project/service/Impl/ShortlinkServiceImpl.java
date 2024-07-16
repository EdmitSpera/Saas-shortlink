package com.nageoffer.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDo;
import com.nageoffer.shortlink.project.dao.mapper.LinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class ShortlinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDo> implements ShortlinkService {

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        // 根据源URL生成短链接
        String shortLinkSuffix = generateSuffix(requestParam);

        // 构造LinkDo 插入到数据库
        ShortLinkDo shortLinkDo = BeanUtil.toBean(requestParam, ShortLinkDo.class);
        shortLinkDo.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        shortLinkDo.setShortUri(shortLinkSuffix);
        baseMapper.insert(shortLinkDo);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(shortLinkDo.getFullShortUrl())
                .build();
    }

    public String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        return HashUtil.hashToBase62(originUrl);
    }
}
