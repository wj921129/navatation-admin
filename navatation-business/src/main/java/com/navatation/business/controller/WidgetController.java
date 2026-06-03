package com.navatation.business.controller;

import com.navatation.business.dto.WidgetRequest;
import com.navatation.business.dto.WidgetVO;
import com.navatation.business.service.WidgetService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件控制器，处理桌面上自定义组件的查询与覆盖保存
 */
@RestController
@RequestMapping("/api/v1/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private static final Logger log = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService widgetService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 获取当前登录用户的所有组件列表
     *
     * @param auth 授权请求头
     * @return 统一响应体封装的组件展示VO列表
     */
    @GetMapping
    public Result<List<WidgetVO>> getWidgets(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取用户组件列表 userId={}", userId);
        List<WidgetVO> result = widgetService.getWidgets(userId);
        return Result.success(result);
    }

    /**
     * 覆盖保存当前登录用户的所有小组件配置列表
     *
     * @param auth 授权请求头
     * @param requests 组件配置保存请求载荷列表
     * @return 统一响应体，提示保存成功
     */
    @PutMapping
    public Result<?> saveWidgets(@RequestHeader("Authorization") String auth,
                                  @RequestBody List<WidgetRequest> requests) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("批量覆盖保存用户组件 userId={}, count={}", userId, requests.size());
        widgetService.saveWidgets(userId, requests);
        return Result.success("组件配置保存成功", null);
    }
}
