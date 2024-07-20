package com.nageoffer.shortlink.project.controller;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.project.service.ShortlinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortlinkController {

    private final ShortlinkService shortlinkService;

    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri")String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortlinkService.restoreUrl(shortUri,request,response);
    }
    /**
     * 新增短链
     * @param requestParam 请求参数，包含创建短链所需的信息
     * @return 包含短链创建结果的响应对象
     */
    @PostMapping("/api/short-link/project/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortlinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链
     * @param requestParam 请求参数，包含分页查询所需的信息
     * @return 包含分页查询结果的响应对象
     */
    @GetMapping("/api/short-link/project/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(@RequestBody ShortLinkPageReqDTO requestParam){
        return Results.success(shortlinkService.pageShortLink(requestParam));
    }

    /**
     * 修改短链
     * @param requestParam
     * @return
     */
    @PutMapping("/api/short-link/project/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortlinkService.updateShortLink(requestParam);
        return Results.success();
    }
}
