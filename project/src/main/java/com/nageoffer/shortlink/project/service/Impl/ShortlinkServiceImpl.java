package com.nageoffer.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.common.enums.VailDateTypeEnum;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDo;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGoto;
import com.nageoffer.shortlink.project.dao.mapper.LinkMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import com.nageoffer.shortlink.project.util.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.nageoffer.shortlink.project.common.convention.errorcode.BaseErrorCode.SERVICE_TIMEOUT_ERROR;
import static com.nageoffer.shortlink.project.common.enums.LinkErrorCodeEnum.LINK_CREATE_ALREADY;

@Service
@RequiredArgsConstructor
public class ShortlinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDo> implements ShortlinkService {

    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 短链接创建响应DTO
     */
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        if (stringRedisTemplate.opsForValue().get(requestParam.getOriginUrl()) != null) {
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

        ShortLinkGoto shortLinkGoto = new ShortLinkGoto().builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            // 在生成shortLinkSuffix时“保证”了该shortLinkSuffix不存在数据库，为什么还要加一层异常处理呢？
            // 个人理解是，可能存在了数据插入到数据库但还没来得及加到布隆过滤器中
            // 可能是进程挂回滚
            // 也可能是极端高并发的场景下导致的掉了 由于这里没有加事务，入库的操作还没来得及
            // 所以使用异常处理捕获数据库中的数据约束，重复数据的异常，进行兜底
            baseMapper.insert(shortLinkDo);
            shortLinkGotoMapper.insert(shortLinkGoto);
            stringRedisTemplate.opsForValue().set(requestParam.getOriginUrl(), "0");
        } catch (DuplicateKeyException ex) {
            String errorMsg = "短链接:" + fullShortUrl + "重复入库";
            log.warn(errorMsg);
            throw new ServiceException("短链接生成重复");
        }

        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl("http://" + shortLinkDo.getFullShortUrl())
                .build();
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页请求参数
     * @return 短链接分页响应DTO
     */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                .eq(ShortLinkDo::getGid, requestParam.getGid())
                .eq(ShortLinkDo::getDelFlag, 0)
                .eq(ShortLinkDo::getEnableStatus, 0)
                .orderByDesc(ShortLinkDo::getCreateTime);


        IPage<ShortLinkDo> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        // 用于将分页查询结果转换为另一种类型的列表。
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    /**
     * 生成短链接后缀
     *
     * @param requestParam 请求参数
     * @return 短链接后缀
     */
    public String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUri = null;
        while (true) {
            // 最大重试次数为10
            if (customGenerateCount >= 10) {
                throw new ClientException(SERVICE_TIMEOUT_ERROR);
            }
            // 加上时间戳来进行哈希
            String originUrlWithTime = requestParam.getOriginUrl() + UUID.randomUUID();
            shortUri = HashUtil.hashToBase62(originUrlWithTime);

            // 使用布隆过滤器进行重复判断
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }

    /**
     * 修改短链接
     *
     * @param requestParam
     */
    @Transactional(rollbackFor = Exception.class)   // 事务
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 在持久层中是使用gid作为分片键的，如果修改了组的话，可能会造成数据丢失
        LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                // 查四个不能变的参数
                .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDo::getDelFlag, 0)
                .eq(ShortLinkDo::getEnableStatus, 0);

        ShortLinkDo hasShortLinkDo = baseMapper.selectOne(queryWrapper);

        if (hasShortLinkDo == null) {
            throw new ClientException("短链接记录不存在");
        }
        // TODO 缓存穿透问题
        // 同组走更新逻辑
        ShortLinkDo shortLinkDo = ShortLinkDo.builder()
                .domain(hasShortLinkDo.getDomain())
                .shortUri(hasShortLinkDo.getShortUri())
                .clickNum(hasShortLinkDo.getClickNum())
                .favicon(hasShortLinkDo.getFavicon())
                .gid(requestParam.getGid())     // Gid要进行讨论
                .originUrl(requestParam.getOriginUrl())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();

        if (Objects.equals(hasShortLinkDo.getGid(), requestParam.getGid())) {   // 查询的和修改的gid是一致的
            LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                    .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDo::getGid, requestParam.getGid())
                    .eq(ShortLinkDo::getDelFlag, 0)
                    .eq(ShortLinkDo::getEnableStatus, 0)
                    .set((Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType())), ShortLinkDo::getValidDate, null);

            baseMapper.update(shortLinkDo, updateWrapper);
        } else {
            // 先删除后新增
            LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                    .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDo::getGid, hasShortLinkDo.getGid())
                    .eq(ShortLinkDo::getDelFlag, 0)
                    .eq(ShortLinkDo::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDo);
        }
    }

    /**
     * 短链接跳转
     * @param shortUri 短链接后缀
     * @param request  短链接请求
     * @param response 短链接响应
     * @throws IOException
     */
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        LambdaQueryWrapper<ShortLinkGoto> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGoto.class)
                .eq(ShortLinkGoto::getFullShortUrl, fullShortUrl);
        ShortLinkGoto shortLinkGoto = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);

        if (shortLinkGoto == null) {
            // TODO 封控？
            return;
        }
        LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                // TODO 传进来的只有shortUri，没有Gid，这样的话无法查到对应分片的数据库
                //  -> goto路由表解决 根据short_url找到对应的分片键
                //  借鉴这种策略的思想也能很好地解决分表的“读扩散问题” -> 建立一个路由索引
                .eq(ShortLinkDo::getGid, shortLinkGoto.getGid())
                .eq(ShortLinkDo::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDo::getDelFlag, 0)
                .eq(ShortLinkDo::getEnableStatus, 0);
        ShortLinkDo shortLinkDo = baseMapper.selectOne(queryWrapper);
        if (shortLinkDo != null) {
            // 进行跳转
            ((HttpServletResponse) response).sendRedirect(shortLinkDo.getOriginUrl());
        }
    }
}
