package com.navatation.business.controller;

import com.navatation.business.dto.RecommendWidgetRequest;
import com.navatation.business.dto.resp.RecommendWidgetRespDTO;
import com.navatation.business.service.RecommendWidgetService;
import com.navatation.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author wanggy
 * @CreateTime 2026-06-08
 * @Description 推荐小组件控制器
 */
@RestController
@RequestMapping("/admin/recommend/widget")
@RequiredArgsConstructor
public class RecommendWidgetController {

    private final RecommendWidgetService recommendWidgetService;

    /**
     * 获取推荐小组件列表
     */
    @GetMapping("/list")
    public Result<List<RecommendWidgetRespDTO>> list() {
        List<RecommendWidgetRespDTO> list = recommendWidgetService.getRecommendWidgets();
        return Result.success(list);
    }

    /**
     * 批量保存推荐小组件
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody List<RecommendWidgetRequest> requests) {
        recommendWidgetService.saveRecommendWidgets(requests);
        return Result.success(null);
    }
}
