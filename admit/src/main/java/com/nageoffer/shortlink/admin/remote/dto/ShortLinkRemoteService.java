package com.nageoffer.shortlink.admin.remote.dto;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

public interface ShortLinkRemoteService {
    default Result<ShortLinkCreateRespDTO>createShortLink(ShortLinkCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/project/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr,new TypeReference<>(){});
        // 这个TypeReference是解决泛型中类型擦除无法判断是什么类型
    }

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        Map<String,Object> request = new HashMap<>();
        request.put("gid",requestParam.getGid());
        request.put("orderTag",requestParam.getOrderTag());
        request.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/project/v1/page",request);

        return JSON.parseObject(resultPageStr,new TypeReference<>(){});
    }
}
