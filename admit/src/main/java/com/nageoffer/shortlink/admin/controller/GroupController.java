package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.ShortlinkGroupSaveReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortlinkGroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOS = groupService.listGroup();
        return Results.success(shortLinkGroupRespDTOS);
    }

}
