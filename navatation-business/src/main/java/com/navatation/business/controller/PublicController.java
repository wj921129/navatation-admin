package com.navatation.business.controller;

import com.navatation.business.dto.resp.settings.GuestConfigRespDTO;
import com.navatation.business.service.PublicService;
import com.navatation.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 公共接口控制器，处理不需要鉴权的请求
 *
 * @date 2026-06-09
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final PublicService publicService;

    /**
     * 获取访客模式下的默认配置
     */
    @GetMapping("/guest-config")
    public Result<GuestConfigRespDTO> getGuestConfig() {
        log.info("HIT PUBLIC CONTROLLER GUEST CONFIG!");
        return Result.success(publicService.getGuestConfig());
    }
}
