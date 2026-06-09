package com.navatation.business.service;

import com.navatation.business.helper.FaviconFetcherHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.dto.resp.nav.FaviconRespDTO;
import com.navatation.business.dto.resp.nav.BatchCreateItemRespDTO;
import com.navatation.business.dto.req.recommend.BatchRecommendSiteSaveReqDTO;
import com.navatation.business.dto.req.recommend.RecommendSiteReqDTO;
import com.navatation.business.dto.req.nav.SortReqDTO;
import com.navatation.business.dto.req.nav.UpdateShortcutReqDTO;
import com.navatation.business.dto.req.nav.BatchCreateReqDTO;
import com.navatation.business.dto.resp.nav.IconUploadRespDTO;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendCategoryRespDTO;
import com.navatation.business.dto.req.nav.CategoryReqDTO;

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

        /**
     * getCategories 方法
     */
    public List<CategoryRespDTO> getCategories(String userId) {
        return navCategoryService.getCategories(userId);
    }

        /**
     * createCategory 方法
     */
    public CategoryRespDTO createCategory(String userId, CategoryReqDTO req) {
        return navCategoryService.createCategory(userId, req);
    }

        /**
     * updateCategory 方法
     */
    public void updateCategory(String userId, String categoryId, CategoryReqDTO req) {
        navCategoryService.updateCategory(userId, categoryId, req);
    }

        /**
     * deleteCategory 方法
     */
    public void deleteCategory(String userId, String categoryId) {
        // NavCategoryService 内部已处理级联删除 shortcut
        navCategoryService.deleteCategory(userId, categoryId);
    }

    // --- Shortcut Management ---

        /**
     * getShortcuts 方法
     */
    public List<ShortcutRespDTO> getShortcuts(String userId, String categoryId) {
        return navShortcutService.getShortcuts(userId, categoryId);
    }

        /**
     * batchCreate 方法
     */
    public List<BatchCreateItemRespDTO> batchCreate(String userId, BatchCreateReqDTO req) {
        return navShortcutService.batchCreate(userId, req);
    }

        /**
     * updateShortcut 方法
     */
    public ShortcutRespDTO updateShortcut(String userId, String shortcutId, UpdateShortcutReqDTO req) {
        return navShortcutService.updateShortcut(userId, shortcutId, req);
    }

        /**
     * deleteShortcut 方法
     */
    public void deleteShortcut(String userId, String shortcutId) {
        navShortcutService.deleteShortcut(userId, shortcutId);
    }

        /**
     * sortShortcuts 方法
     */
    public void sortShortcuts(String userId, SortReqDTO req) {
        navShortcutService.sortShortcuts(userId, req);
    }

    // --- Favicon & Icon Upload ---

        /**
     * fetchFavicon 方法
     */
    public FaviconRespDTO fetchFavicon(String url) {
        return faviconFetcherHelper.fetchFavicon(url);
    }

    public Map<String, FaviconRespDTO> fetchFaviconsInBatch(List<String> urls) {
        return faviconFetcherHelper.fetchFaviconsInBatch(urls);
    }

        /**
     * uploadIcon 方法
     */
    public IconUploadRespDTO uploadIcon(String userId, MultipartFile file) {
        return iconUploadService.uploadIcon(userId, file);
    }

    // --- Recommend Sites Management ---

        /**
     * getRecommended 方法
     */
    public List<RecommendCategoryRespDTO> getRecommended() {
        return navCategoryService.getRecommended();
    }

        /**
     * addRecommendCategory 方法
     */
    public RecommendCategoryRespDTO addRecommendCategory(String userId, RecommendCategoryReqDTO req) {
        return navCategoryService.addRecommendCategory(userId, req);
    }

        /**
     * updateRecommendCategory 方法
     */
    public void updateRecommendCategory(String userId, String categoryId, RecommendCategoryReqDTO req) {
        navCategoryService.updateRecommendCategory(userId, categoryId, req);
    }

        /**
     * deleteRecommendCategory 方法
     */
    public void deleteRecommendCategory(String userId, String categoryId) {
        navCategoryService.deleteRecommendCategory(userId, categoryId);
    }

        /**
     * addRecommendSite 方法
     */
    public RecommendSiteRespDTO addRecommendSite(String userId, RecommendSiteReqDTO req) {
        return navShortcutService.addRecommendSite(userId, req);
    }

        /**
     * updateRecommendSite 方法
     */
    public void updateRecommendSite(String userId, String siteId, RecommendSiteReqDTO req) {
        navShortcutService.updateRecommendSite(userId, siteId, req);
    }

        /**
     * deleteRecommendSite 方法
     */
    public void deleteRecommendSite(String userId, String siteId) {
        navShortcutService.deleteRecommendSite(userId, siteId);
    }

        /**
     * batchSaveRecommendSites 方法
     */
    public void batchSaveRecommendSites(String userId, String categoryId, BatchRecommendSiteSaveReqDTO req) {
        navShortcutService.batchSaveRecommendSites(userId, categoryId, req);
    }
}
