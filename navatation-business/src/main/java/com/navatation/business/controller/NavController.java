package com.navatation.business.controller;

import com.navatation.business.dto.BatchCreateItemVO;
import com.navatation.business.dto.BatchCreateRequest;
import com.navatation.business.dto.CategoryRequest;
import com.navatation.business.dto.CategoryVO;
import com.navatation.business.dto.FaviconRequest;
import com.navatation.business.dto.FaviconVO;
import com.navatation.business.dto.IconUploadVO;
import com.navatation.business.dto.RecommendCategoryVO;
import com.navatation.business.dto.ShortcutVO;
import com.navatation.business.dto.SortRequest;
import com.navatation.business.dto.UpdateShortcutRequest;
import com.navatation.business.dto.RecommendCategoryRequest;
import com.navatation.business.dto.RecommendSiteRequest;
import com.navatation.business.dto.RecommendSiteVO;
import com.navatation.business.service.NavService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航控制器，处理分类和快捷方式的CRUD、排序、Favicon获取及推荐站点
 */
@RestController
@RequestMapping("/api/v1/nav")
@RequiredArgsConstructor
public class NavController {

    private static final Logger log = LoggerFactory.getLogger(NavController.class);

    private final NavService navService;
    private final JwtTokenProvider jwtTokenProvider;

    // ---- Category ----
    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取分类列表 入参:userId={}", userId);
        List<CategoryVO> result = navService.getCategories(userId);
        log.info("获取分类列表 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/categories")
    public Result<CategoryVO> createCategory(@RequestHeader("Authorization") String auth,
                                              @RequestBody CategoryRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("创建分类 入参:userId={},name={}", userId, req.getName());
        CategoryVO result = navService.createCategory(userId, req);
        log.info("创建分类 出参:categoryId={}", result.getCategoryId());
        return Result.success("创建成功", result);
    }

    @PutMapping("/categories/{categoryId}")
    public Result<?> updateCategory(@RequestHeader("Authorization") String auth,
                                     @PathVariable String categoryId,
                                     @RequestBody CategoryRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新分类 入参:userId={},categoryId={}", userId, categoryId);
        navService.updateCategory(userId, categoryId, req);
        log.info("更新分类 出参:success=true");
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/categories/{categoryId}")
    public Result<?> deleteCategory(@RequestHeader("Authorization") String auth,
                                     @PathVariable String categoryId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除分类 入参:userId={},categoryId={}", userId, categoryId);
        navService.deleteCategory(userId, categoryId);
        log.info("删除分类 出参:success=true");
        return Result.success("删除成功", null);
    }

    // ---- Shortcut ----
    @GetMapping("/shortcuts")
    public Result<List<ShortcutVO>> getShortcuts(@RequestHeader("Authorization") String auth,
                                                  @RequestParam(required = false) String categoryId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取快捷方式列表 入参:userId={},categoryId={}", userId, categoryId);
        List<ShortcutVO> result = navService.getShortcuts(userId, categoryId);
        log.info("获取快捷方式列表 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/shortcuts/batch")
    public Result<List<BatchCreateItemVO>> batchCreate(@RequestHeader("Authorization") String auth,
                                                 @RequestBody BatchCreateRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("批量创建快捷方式 入参:userId={},count={}", userId, req.getShortcuts().size());
        List<BatchCreateItemVO> result = navService.batchCreate(userId, req);
        log.info("批量创建快捷方式 出参:count={}", result.size());
        return Result.success("成功添加 " + result.size() + " 个快捷方式", result);
    }

    @PutMapping("/shortcuts/{shortcutId}")
    public Result<ShortcutVO> updateShortcut(@RequestHeader("Authorization") String auth,
                                              @PathVariable String shortcutId,
                                              @RequestBody UpdateShortcutRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新快捷方式 入参:userId={},shortcutId={}", userId, shortcutId);
        ShortcutVO result = navService.updateShortcut(userId, shortcutId, req);
        log.info("更新快捷方式 出参:name={}", result.getName());
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/shortcuts/{shortcutId}")
    public Result<?> deleteShortcut(@RequestHeader("Authorization") String auth,
                                     @PathVariable String shortcutId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除快捷方式 入参:userId={},shortcutId={}", userId, shortcutId);
        navService.deleteShortcut(userId, shortcutId);
        log.info("删除快捷方式 出参:success=true");
        return Result.success("删除成功", null);
    }

    @PutMapping("/shortcuts/sort")
    public Result<?> sortShortcuts(@RequestHeader("Authorization") String auth,
                                    @RequestBody SortRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("快捷方式排序 入参:userId={},count={}", userId, req.getItems().size());
        navService.sortShortcuts(userId, req);
        log.info("快捷方式排序 出参:success=true");
        return Result.success("排序更新成功", null);
    }

    // ---- Favicon ----
    @PostMapping("/favicon")
    public Result<FaviconVO> fetchFavicon(@RequestHeader("Authorization") String auth,
                                           @RequestBody FaviconRequest req) {
        log.info("获取Favicon 入参:url={}", req.getUrl());
        FaviconVO result = navService.fetchFavicon(req.getUrl());
        log.info("获取Favicon 出参:faviconUrl={}", result.getFaviconUrl());
        return Result.success(result);
    }

    // ---- Icon Upload ----
    @PostMapping("/icon/upload")
    public Result<IconUploadVO> uploadIcon(@RequestHeader("Authorization") String auth,
                                            @RequestParam("file") MultipartFile file) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("上传图标 入参:userId={},filename={},size={}", userId, file.getOriginalFilename(), file.getSize());
        IconUploadVO result = navService.uploadIcon(userId, file);
        log.info("上传图标 出参:iconUrl={}", result.getIconUrl());
        return Result.success("上传成功", result);
    }

    // ---- Recommended ----
    @GetMapping("/recommended")
    public Result<List<RecommendCategoryVO>> getRecommended() {
        log.info("获取推荐站点");
        List<RecommendCategoryVO> result = navService.getRecommended();
        log.info("获取推荐站点 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/recommended/categories")
    public Result<RecommendCategoryVO> addRecommendCategory(@RequestHeader("Authorization") String auth,
                                                            @RequestBody RecommendCategoryRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("添加推荐分类 入参:userId={},name={}", userId, req.getName());
        RecommendCategoryVO result = navService.addRecommendCategory(userId, req);
        return Result.success("添加成功", result);
    }

    @PutMapping("/recommended/categories/{categoryId}")
    public Result<?> updateRecommendCategory(@RequestHeader("Authorization") String auth,
                                             @PathVariable String categoryId,
                                             @RequestBody RecommendCategoryRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新推荐分类 入参:userId={},categoryId={}", userId, categoryId);
        navService.updateRecommendCategory(userId, categoryId, req);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/recommended/categories/{categoryId}")
    public Result<?> deleteRecommendCategory(@RequestHeader("Authorization") String auth,
                                             @PathVariable String categoryId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除推荐分类 入参:userId={},categoryId={}", userId, categoryId);
        navService.deleteRecommendCategory(userId, categoryId);
        return Result.success("删除成功", null);
    }

    @PostMapping("/recommended/sites")
    public Result<RecommendSiteVO> addRecommendSite(@RequestHeader("Authorization") String auth,
                                                    @RequestBody RecommendSiteRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("添加推荐网址 入参:userId={},name={}", userId, req.getName());
        RecommendSiteVO result = navService.addRecommendSite(userId, req);
        return Result.success("添加成功", result);
    }

    @PutMapping("/recommended/sites/{siteId}")
    public Result<?> updateRecommendSite(@RequestHeader("Authorization") String auth,
                                         @PathVariable String siteId,
                                         @RequestBody RecommendSiteRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新推荐网址 入参:userId={},siteId={}", userId, siteId);
        navService.updateRecommendSite(userId, siteId, req);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/recommended/sites/{siteId}")
    public Result<?> deleteRecommendSite(@RequestHeader("Authorization") String auth,
                                         @PathVariable String siteId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除推荐网址 入参:userId={},siteId={}", userId, siteId);
        navService.deleteRecommendSite(userId, siteId);
        return Result.success("删除成功", null);
    }
}
