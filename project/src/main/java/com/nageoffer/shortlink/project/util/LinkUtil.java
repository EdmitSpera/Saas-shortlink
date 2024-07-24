package com.nageoffer.shortlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.nageoffer.shortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * 短链接工具类
 */
public class LinkUtil {

    // 获取有效日期
    // 计算 validDate 与当前日期之间的时间差（以毫秒为单位），如果 validDate 为空，则返回一个默认的缓存有效时间 DEFAULT_CACHE_VALID_TIME
    public static long getLinkCacheValidData(Date validDate){
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS))
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
