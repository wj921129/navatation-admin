package com.navatation.business.controller;

import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;
import com.navatation.business.dto.resp.nav.HomeShortcutRespDTO;
import com.navatation.business.dto.resp.settings.SettingsRespDTO;
import com.navatation.business.dto.resp.widget.WidgetRespDTO;
import com.navatation.business.service.PublicService;
import com.navatation.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import java.util.List;

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
     * 获取访客模式下的默认设置
     */
    @GetMapping("/guest-settings")
    public Result<SettingsRespDTO> getGuestSettings() {
        return Result.success(publicService.getGuestSettings());
    }

    /**
     * 获取访客模式下的默认小组件
     */
    @GetMapping("/guest-widgets")
    public Result<List<WidgetRespDTO>> getGuestWidgets() {
        return Result.success(publicService.getGuestWidgets());
    }

    /**
     * 获取访客模式下的默认分类
     */
    @GetMapping("/guest-categories")
    public Result<List<CategoryRespDTO>> getGuestCategories() {
        return Result.success(publicService.getGuestCategories());
    }

    /**
     * 获取访客模式下的默认快捷方式
     */
    @GetMapping("/guest-shortcuts")
    public Result<List<ShortcutRespDTO>> getGuestShortcuts() {
        return Result.success(publicService.getGuestShortcuts());
    }

    /**
     * 获取访客模式下的默认待办事项
     */
    @GetMapping("/guest-todos")
    public Result<List<com.navatation.business.dto.resp.todo.TodoRespDTO>> getGuestTodos() {
        return Result.success(publicService.getGuestTodos());
    }

    /**
     * 获取访客模式下的默认首页网址
     */
    @GetMapping("/guest-home-shortcuts")
    public Result<List<HomeShortcutRespDTO>> getGuestHomeShortcuts() {
        return Result.success(publicService.getGuestHomeShortcuts());
    }
}
