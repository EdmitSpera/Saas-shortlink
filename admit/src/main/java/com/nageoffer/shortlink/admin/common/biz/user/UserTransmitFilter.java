package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.nageoffer.shortlink.admin.service.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    // 需要放行的url
    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/shortlink/v1/user/login",
            "/api/shortlink/v1/user/register",
            "/api/shortlink/v1/user/check-login"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 对登录接口进行放行
        String requestURL = httpServletRequest.getRequestURI();
        if(!IGNORE_URI.contains(requestURL)){

            // 判断用户是否登录 需要获取当前的username和token
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");

            // 从Redis中获取到已经登录的实体对象DTO
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);

            if (userInfoJsonStr != null) {
                // 如果已经登录 将用户信息加入用户上下文中
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 最后防止内存泄露
            UserContext.removeUser();
        }
    }
}