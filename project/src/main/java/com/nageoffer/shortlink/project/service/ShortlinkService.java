package com.nageoffer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDo;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.springframework.stereotype.Service;


public interface ShortlinkService extends IService<ShortLinkDo> {

    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

}
