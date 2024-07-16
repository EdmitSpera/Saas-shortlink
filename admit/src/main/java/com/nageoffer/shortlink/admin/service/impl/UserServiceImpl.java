package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
import com.nageoffer.shortlink.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import jodd.template.StringTemplateParser;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.*;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    // 非法用户名拦截布隆过滤器
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 根据用户名从数据库中查询用户信息并返回响应实体
     *
     * @param username 用户名
     * @return 用户信息响应实体
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(queryWrapper);

        // 如果数据库中没有找到该对象，抛出异常，全局拦截器进行拦截
        if (userDo == null) {
            throw new ClientException(USER_NULL);
        }

        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDo, result);
        return result;
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    /**
     * 用户注册方法
     *
     * @param requestParam 注册请求参数
     */
    @Override
    public void Register(UserRegisterReqDTO requestParam) {
        // 如果用户名存在 抛出异常
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        // 获取分布式锁
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
            if (lock.tryLock()) {
                // 如果插入数据库的数据小于1，说明注册失败，已经有这条用户数据，也抛出异常
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDo.class));
                if (insert < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                // 添加到布隆过滤器 预防缓存穿透
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            } else {
                throw new ClientException(USER_SAVE_ERROR);
            }
        } finally {
            // 释放分布式锁
            lock.unlock();
        }
    }

    /**
     * 更新用户信息
     *
     * @param requestParam 更新请求参数
     */
    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO 如果用户未登录，抛出异常进行拦截
        LambdaUpdateWrapper<UserDo> updateWrapper = Wrappers.lambdaUpdate(UserDo.class)
                .eq(UserDo::getUsername, requestParam.getUsername());
        // 根据用户名定位到语句
        baseMapper.update(BeanUtil.toBean(requestParam, UserDo.class), updateWrapper);
    }

    /**
     * 用户登录方法
     *
     * @param requestParam 登录请求参数
     * @return 登录响应结果
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 如果用户已经登录，则抛出用户登录的异常，捕获信息
        String requestUsernameKey = "login_" + requestParam.getUsername();
        if (redissonClient.getKeys().countExists(requestUsernameKey) >= 1) {
            throw new ClientException(USER_LOGIN_ALREADY);
        }

        // 从数据库中找到当前的用户
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername, requestParam.getUsername())
                .eq(UserDo::getPassword, requestParam.getPassword())
                .eq(UserDo::getDelFlag, 0);

        UserDo userDo = baseMapper.selectOne(queryWrapper);

        // 根据用户名和密码验证了用户成功后，生成token
        if (userDo == null) {
            throw new ClientException(USER_NULL);
        }

        String loginKey = "login_" + userDo.getUsername();
        // 以UUID作为token
        UUID token = UUID.randomUUID();

        // 将已登录的用户信息存放到Redis中
        stringRedisTemplate.opsForHash().put(loginKey, token.toString(), JSON.toJSONString(userDo));
        stringRedisTemplate.expire(loginKey, 30, TimeUnit.DAYS);
        return new UserLoginRespDTO(token.toString());
    }

    /**
     * 检查用户登录状态
     *
     * @param username 用户名
     * @param token    用户令牌
     * @return 登录状态
     */
    @Override
    public Boolean checkLoginStatus(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_" + username, token) != null;
    }

    @Override
    public Boolean logout(String username, String token) {
        Boolean loginStatus = checkLoginStatus(username, token);
        if (!loginStatus) {
            throw new ClientException(USER_LOGIN_NULL);
        }
        stringRedisTemplate.opsForHash().delete("login_" + username, token);
        return true;
    }
}
