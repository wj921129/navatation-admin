package com.navatation.business.controller;

import com.navatation.business.dto.resp.settings.GuestConfigRespDTO;
import com.navatation.business.service.PublicService;
import com.navatation.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PublicController 功能描述
 *
 * @date 2026-06-09
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

        /**
     * getGuestConfig 方法
     */
    @GetMapping("/guest-config")
    public Result<GuestConfigRespDTO> getGuestConfig() {
        System.out.println("HIT PUBLIC CONTROLLER GUEST CONFIG!");
        return Result.success(publicService.getGuestConfig());
    }
}
