package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkDeleteReqDTO {
    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;


    /**
     * 分组标识
     */
    private String gid;

}
