package com.nageoffer.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDo;
import com.nageoffer.shortlink.project.dao.mapper.LinkMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.nageoffer.shortlink.project.common.enums.LinkErrorCodeEnum.LINK_CREATE_ALREADY;
import static com.nageoffer.shortlink.project.common.convention.errorcode.BaseErrorCode.SERVICE_TIMEOUT_ERROR;

@Service
@RequiredArgsConstructor
public class ShortlinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDo> implements ShortlinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        if(stringRedisTemplate.opsForValue().get(requestParam.getOriginUrl()) != null){
            // 说明源链接已经生成了，所以不生成
            throw new ClientException(LINK_CREATE_ALREADY);
        }
        // 根据源URL生成短链接
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;

        // 构造LinkDo 插入到数据库
        ShortLinkDo shortLinkDo = BeanUtil.toBean(requestParam, ShortLinkDo.class);
        shortLinkDo.setFullShortUrl(fullShortUrl);
        shortLinkDo.setShortUri(shortLinkSuffix);

        try {
            // 在生成shortLinkSuffix时“保证”了该shortLinkSuffix不存在数据库，为什么还要加一层异常处理呢？
            // 个人理解是，可能存在了数据插入到数据库但还没来得及加到布隆过滤器中
            // 可能是进程挂掉了 由于这里没有加事务，入库的操作还没来得及回滚
            // 也可能是极端高并发的场景下导致的
            // 所以使用异常处理捕获数据库中的数据约束，重复数据的异常，进行兜底
            baseMapper.insert(shortLinkDo);
            stringRedisTemplate.opsForValue().set(requestParam.getOriginUrl(), "0");
        }catch (DuplicateKeyException ex){
                String errorMsg = "短链接:" + fullShortUrl + "重复入库";
                log.warn(errorMsg);
                throw new ServiceException("短链接生成重复");
        }

        shortUriCreateCachePenetrationBloomFilter.add(shortLinkSuffix);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(shortLinkDo.getFullShortUrl())
                .build();
    }

    public String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUri = null;
        while(true){
            // 最大重试次数为10
            if(customGenerateCount == 10){
                throw new ClientException(SERVICE_TIMEOUT_ERROR);
            }
            // 加上时间戳来进行哈希
            String originUrlWithTime = requestParam.getOriginUrl() + UUID.randomUUID();
            shortUri = HashUtil.hashToBase62(originUrlWithTime);

            // 使用布隆过滤器进行重复判断
            if(!shortUriCreateCachePenetrationBloomFilter.contains(shortUri)){
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }
}
