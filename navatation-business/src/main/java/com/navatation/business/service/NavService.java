package com.navatation.business.service;

import com.navatation.business.dto.*;
import com.navatation.business.helper.FaviconFetcherHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航服务门面（Facade），代理底层的分类、快捷方式和图标上传服务
 */
@Service
@RequiredArgsConstructor
public class NavService {

    private final NavCategoryService navCategoryService;
    private final NavShortcutService navShortcutService;
    private final FaviconFetcherHelper faviconFetcherHelper;
    private final IconUploadService iconUploadService;

    // --- Category Management ---

    public List<CategoryVO> getCategories(String userId) {
        return navCategoryService.getCategories(userId);
    }

    public CategoryVO createCategory(String userId, CategoryRequest req) {
        return navCategoryService.createCategory(userId, req);
    }

    public void updateCategory(String userId, String categoryId, CategoryRequest req) {
        navCategoryService.updateCategory(userId, categoryId, req);
    }

    public void deleteCategory(String userId, String categoryId) {
        // NavCategoryService 内部已处理级联删除 shortcut
        navCategoryService.deleteCategory(userId, categoryId);
    }

    // --- Shortcut Management ---

    public List<ShortcutVO> getShortcuts(String userId, String categoryId) {
        return navShortcutService.getShortcuts(userId, categoryId);
    }

    public List<BatchCreateItemVO> batchCreate(String userId, BatchCreateRequest req) {
        return navShortcutService.batchCreate(userId, req);
    }

    public ShortcutVO updateShortcut(String userId, String shortcutId, UpdateShortcutRequest req) {
        return navShortcutService.updateShortcut(userId, shortcutId, req);
    }

    public void deleteShortcut(String userId, String shortcutId) {
        navShortcutService.deleteShortcut(userId, shortcutId);
    }

    public void sortShortcuts(String userId, SortRequest req) {
        navShortcutService.sortShortcuts(userId, req);
    }

    // --- Favicon & Icon Upload ---

    public FaviconVO fetchFavicon(String url) {
        return faviconFetcherHelper.fetchFavicon(url);
    }

    public Map<String, FaviconVO> fetchFaviconsInBatch(List<String> urls) {
        return faviconFetcherHelper.fetchFaviconsInBatch(urls);
    }

    public IconUploadVO uploadIcon(String userId, MultipartFile file) {
        return iconUploadService.uploadIcon(userId, file);
    }

    // --- Recommend Sites Management ---

    public List<RecommendCategoryVO> getRecommended() {
        return navCategoryService.getRecommended();
    }

    public RecommendCategoryVO addRecommendCategory(String userId, RecommendCategoryRequest req) {
        return navCategoryService.addRecommendCategory(userId, req);
    }

    public void updateRecommendCategory(String userId, String categoryId, RecommendCategoryRequest req) {
        navCategoryService.updateRecommendCategory(userId, categoryId, req);
    }

    public void deleteRecommendCategory(String userId, String categoryId) {
        navCategoryService.deleteRecommendCategory(userId, categoryId);
    }

    public RecommendSiteVO addRecommendSite(String userId, RecommendSiteRequest req) {
        return navShortcutService.addRecommendSite(userId, req);
    }

    public void updateRecommendSite(String userId, String siteId, RecommendSiteRequest req) {
        navShortcutService.updateRecommendSite(userId, siteId, req);
    }

    public void deleteRecommendSite(String userId, String siteId) {
        navShortcutService.deleteRecommendSite(userId, siteId);
    }

    public void batchSaveRecommendSites(String userId, String categoryId, BatchRecommendSiteSaveRequest req) {
        navShortcutService.batchSaveRecommendSites(userId, categoryId, req);
    }
}
