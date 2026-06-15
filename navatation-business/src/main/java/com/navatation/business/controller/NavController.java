package com.navatation.business.controller;

import com.navatation.business.dto.resp.nav.BatchCreateItemRespDTO;
import com.navatation.business.dto.req.nav.BatchCreateReqDTO;
import com.navatation.business.dto.req.nav.CategoryReqDTO;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.req.nav.FaviconReqDTO;
import com.navatation.business.dto.resp.nav.FaviconRespDTO;
import com.navatation.business.dto.resp.nav.IconUploadRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendCategoryRespDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;
import com.navatation.business.dto.req.nav.SortReqDTO;
import com.navatation.business.dto.req.nav.UpdateShortcutReqDTO;
import com.navatation.business.dto.req.nav.HomeShortcutReqDTO;
import com.navatation.business.dto.resp.nav.HomeShortcutRespDTO;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;
import com.navatation.business.dto.req.recommend.RecommendSiteReqDTO;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.dto.req.recommend.BatchRecommendSiteSaveReqDTO;
import com.navatation.business.dto.req.nav.BatchFaviconReqDTO;
import com.navatation.business.service.NavService;
import com.navatation.business.service.HomeShortcutService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
import jakarta.validation.Valid;
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
import java.util.Map;

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
    private final HomeShortcutService homeShortcutService;
    private final JwtTokenProvider jwtTokenProvider;

    // ---- Category ----
    @GetMapping("/categories")
    public Result<List<CategoryRespDTO>> getCategories(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取分类列表 入参:userId={}", userId);
        List<CategoryRespDTO> result = navService.getCategories(userId);
        log.info("获取分类列表 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/categories")
    public Result<CategoryRespDTO> createCategory(@RequestHeader("Authorization") String auth,
                                              @RequestBody CategoryReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("创建分类 入参:userId={},name={}", userId, req.getName());
        CategoryRespDTO result = navService.createCategory(userId, req);
        log.info("创建分类 出参:categoryId={}", result.getCategoryId());
        return Result.success("创建成功", result);
    }

    @PutMapping("/categories/{categoryId}")
    public Result<?> updateCategory(@RequestHeader("Authorization") String auth,
                                     @PathVariable String categoryId,
                                     @RequestBody CategoryReqDTO req) {
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
    public Result<List<ShortcutRespDTO>> getShortcuts(@RequestHeader("Authorization") String auth,
                                                  @RequestParam(required = false) String categoryId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取快捷方式列表 入参:userId={},categoryId={}", userId, categoryId);
        List<ShortcutRespDTO> result = navService.getShortcuts(userId, categoryId);
        log.info("获取快捷方式列表 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/shortcuts/batch")
    public Result<List<BatchCreateItemRespDTO>> batchCreate(@RequestHeader("Authorization") String auth,
                                                 @RequestBody BatchCreateReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("批量创建快捷方式 入参:userId={},count={}", userId, req.getShortcuts().size());
        List<BatchCreateItemRespDTO> result = navService.batchCreate(userId, req);
        log.info("批量创建快捷方式 出参:count={}", result.size());
        return Result.success("成功添加 " + result.size() + " 个快捷方式", result);
    }

    @PutMapping("/shortcuts/{shortcutId}")
    public Result<ShortcutRespDTO> updateShortcut(@RequestHeader("Authorization") String auth,
                                              @PathVariable String shortcutId,
                                              @RequestBody UpdateShortcutReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新快捷方式 入参:userId={},shortcutId={}", userId, shortcutId);
        ShortcutRespDTO result = navService.updateShortcut(userId, shortcutId, req);
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
                                    @RequestBody SortReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("快捷方式排序 入参:userId={},count={}", userId, req.getItems().size());
        navService.sortShortcuts(userId, req);
        log.info("快捷方式排序 出参:success=true");
        return Result.success("排序更新成功", null);
    }

    // ---- Favicon ----
    @PostMapping("/favicon")
    public Result<FaviconRespDTO> fetchFavicon(@RequestHeader("Authorization") String auth,
                                           @RequestBody FaviconReqDTO req) {
        log.info("获取Favicon 入参:url={}", req.getUrl());
        FaviconRespDTO result = navService.fetchFavicon(req.getUrl());
        log.info("获取Favicon 出参:faviconUrl={}", result.getFaviconUrl());
        return Result.success(result);
    }

    @PostMapping("/favicon/batch")
    public Result<Map<String, FaviconRespDTO>> fetchFaviconsInBatch(@RequestHeader("Authorization") String auth,
                                                               @RequestBody @Valid BatchFaviconReqDTO req) {
        log.info("批量获取Favicon 入参:urls count={}", req.getUrls() != null ? req.getUrls().size() : 0);
        Map<String, FaviconRespDTO> result = navService.fetchFaviconsInBatch(req.getUrls());
        log.info("批量获取Favicon 出参:count={}", result.size());
        return Result.success(result);
    }

    // ---- Icon Upload ----
    @PostMapping("/icon/upload")
    public Result<IconUploadRespDTO> uploadIcon(@RequestHeader("Authorization") String auth,
                                            @RequestParam("file") MultipartFile file) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("上传图标 入参:userId={},filename={},size={}", userId, file.getOriginalFilename(), file.getSize());
        IconUploadRespDTO result = navService.uploadIcon(userId, file);
        log.info("上传图标 出参:iconUrl={}", result.getIconUrl());
        return Result.success("上传成功", result);
    }

    // ---- Recommended ----
        /**
     * 获取推荐站点及分类列表
     */
    @GetMapping("/recommended")
    public Result<List<RecommendCategoryRespDTO>> getRecommended() {
        log.info("获取推荐站点");
        List<RecommendCategoryRespDTO> result = navService.getRecommended();
        log.info("获取推荐站点 出参:count={}", result.size());
        return Result.success(result);
    }

    @PostMapping("/recommended/categories")
    public Result<RecommendCategoryRespDTO> addRecommendCategory(@RequestHeader("Authorization") String auth,
                                                            @RequestBody RecommendCategoryReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("添加推荐分类 入参:userId={},name={}", userId, req.getName());
        RecommendCategoryRespDTO result = navService.addRecommendCategory(userId, req);
        return Result.success("添加成功", result);
    }

    @PutMapping("/recommended/categories/{categoryId}")
    public Result<?> updateRecommendCategory(@RequestHeader("Authorization") String auth,
                                             @PathVariable String categoryId,
                                             @RequestBody RecommendCategoryReqDTO req) {
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
    public Result<RecommendSiteRespDTO> addRecommendSite(@RequestHeader("Authorization") String auth,
                                                    @RequestBody RecommendSiteReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("添加推荐网址 入参:userId={},name={}", userId, req.getName());
        RecommendSiteRespDTO result = navService.addRecommendSite(userId, req);
        return Result.success("添加成功", result);
    }

    @PutMapping("/recommended/sites/{siteId}")
    public Result<?> updateRecommendSite(@RequestHeader("Authorization") String auth,
                                         @PathVariable String siteId,
                                         @RequestBody RecommendSiteReqDTO req) {
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

    /**
     * 批量保存指定分类下的推荐网址
     *
     * @param auth 认证Token
     * @param categoryId 推荐分类ID
     * @param req 批量保存推荐网址的请求数据
     * @return 统一返回结果
     */
    @PostMapping("/recommended/categories/{categoryId}/sites/batch")
    public Result<?> batchSaveRecommendSites(@RequestHeader("Authorization") String auth,
                                             @PathVariable String categoryId,
                                             @RequestBody @Valid BatchRecommendSiteSaveReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("批量保存推荐网址 入参:userId={},categoryId={},count={}", userId, categoryId, req.getSites() != null ? req.getSites().size() : 0);
        navService.batchSaveRecommendSites(userId, categoryId, req);
        log.info("批量保存推荐网址 出参:success=true");
        return Result.success("保存成功", null);
    }

    // ---- Home Shortcut ----
    @GetMapping("/home-shortcuts")
    public Result<List<HomeShortcutRespDTO>> getHomeShortcuts(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取首页网址 入参:userId={}", userId);
        List<HomeShortcutRespDTO> result = homeShortcutService.getHomeShortcuts(userId);
        return Result.success(result);
    }

    @PostMapping("/home-shortcuts")
    public Result<HomeShortcutRespDTO> addHomeShortcut(@RequestHeader("Authorization") String auth,
                                                  @RequestBody HomeShortcutReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("添加首页网址 入参:userId={},name={}", userId, req.getName());
        HomeShortcutRespDTO result = homeShortcutService.addHomeShortcut(userId, req);
        return Result.success("添加成功", result);
    }

    @PutMapping("/home-shortcuts/{shortcutId}")
    public Result<HomeShortcutRespDTO> updateHomeShortcut(@RequestHeader("Authorization") String auth,
                                                     @PathVariable String shortcutId,
                                                     @RequestBody HomeShortcutReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("更新首页网址 入参:userId={},shortcutId={}", userId, shortcutId);
        HomeShortcutRespDTO result = homeShortcutService.updateHomeShortcut(userId, shortcutId, req);
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/home-shortcuts/{shortcutId}")
    public Result<?> deleteHomeShortcut(@RequestHeader("Authorization") String auth,
                                        @PathVariable String shortcutId) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("删除首页网址 入参:userId={},shortcutId={}", userId, shortcutId);
        homeShortcutService.deleteHomeShortcut(userId, shortcutId);
        return Result.success("删除成功", null);
    }
}
