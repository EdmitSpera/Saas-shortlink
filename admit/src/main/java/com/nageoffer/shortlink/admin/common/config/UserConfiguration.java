package com.nageoffer.shortlink.admin.common.config;

import com.nageoffer.shortlink.admin.common.biz.user.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();   // Filter配置类方法需要返回一个FilterRegistrationBean对象
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));        // 设置过滤器
        registration.addUrlPatterns("/*");  // 配置过滤规则
        registration.setOrder(0);   // 过滤器的执行顺序
        return registration;
    }
}
