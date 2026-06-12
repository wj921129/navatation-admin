package com.navatation.business.service;

import com.navatation.business.helper.FaviconFetcherHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.redis.core.RedisTemplate;

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
import com.navatation.business.mapper.RootUserMapper;

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
    private final RootUserMapper rootUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private boolean isAdmin(String userId) {
        return rootUserMapper.selectById(userId) != null;
    }

    // --- Category Management ---

    public List<CategoryRespDTO> getCategories(String userId) {
        return navCategoryService.getCategories(userId);
    }

    public CategoryRespDTO createCategory(String userId, CategoryReqDTO req) {
        CategoryRespDTO res = navCategoryService.createCategory(userId, req);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "categories");
        return res;
    }

    public void updateCategory(String userId, String categoryId, CategoryReqDTO req) {
        navCategoryService.updateCategory(userId, categoryId, req);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "categories");
    }

    public void deleteCategory(String userId, String categoryId) {
        // NavCategoryService 内部已处理级联删除 shortcut
        navCategoryService.deleteCategory(userId, categoryId);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "categories");
    }

    // --- Shortcut Management ---

    public List<ShortcutRespDTO> getShortcuts(String userId, String categoryId) {
        return navShortcutService.getShortcuts(userId, categoryId);
    }

    public List<BatchCreateItemRespDTO> batchCreate(String userId, BatchCreateReqDTO req) {
        List<BatchCreateItemRespDTO> res = navShortcutService.batchCreate(userId, req);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "shortcuts");
        return res;
    }

    public ShortcutRespDTO updateShortcut(String userId, String shortcutId, UpdateShortcutReqDTO req) {
        ShortcutRespDTO res = navShortcutService.updateShortcut(userId, shortcutId, req);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "shortcuts");
        return res;
    }

    public void deleteShortcut(String userId, String shortcutId) {
        navShortcutService.deleteShortcut(userId, shortcutId);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "shortcuts");
    }

    public void sortShortcuts(String userId, SortReqDTO req) {
        navShortcutService.sortShortcuts(userId, req);
        if (isAdmin(userId)) redisTemplate.opsForHash().delete("navatation:guest_config", "shortcuts");
    }

    // --- Favicon & Icon Upload ---

    public FaviconRespDTO fetchFavicon(String url) {
        return faviconFetcherHelper.fetchFavicon(url);
    }

    public Map<String, FaviconRespDTO> fetchFaviconsInBatch(List<String> urls) {
        return faviconFetcherHelper.fetchFaviconsInBatch(urls);
    }

    public IconUploadRespDTO uploadIcon(String userId, MultipartFile file) {
        return iconUploadService.uploadIcon(userId, file);
    }

    // --- Recommend Sites Management ---

    public List<RecommendCategoryRespDTO> getRecommended() {
        return navCategoryService.getRecommended();
    }

    public RecommendCategoryRespDTO addRecommendCategory(String userId, RecommendCategoryReqDTO req) {
        return navCategoryService.addRecommendCategory(userId, req);
    }

    public void updateRecommendCategory(String userId, String categoryId, RecommendCategoryReqDTO req) {
        navCategoryService.updateRecommendCategory(userId, categoryId, req);
    }

    public void deleteRecommendCategory(String userId, String categoryId) {
        navCategoryService.deleteRecommendCategory(userId, categoryId);
    }

    public RecommendSiteRespDTO addRecommendSite(String userId, RecommendSiteReqDTO req) {
        return navShortcutService.addRecommendSite(userId, req);
    }

    public void updateRecommendSite(String userId, String siteId, RecommendSiteReqDTO req) {
        navShortcutService.updateRecommendSite(userId, siteId, req);
    }

    public void deleteRecommendSite(String userId, String siteId) {
        navShortcutService.deleteRecommendSite(userId, siteId);
    }

    public void batchSaveRecommendSites(String userId, String categoryId, BatchRecommendSiteSaveReqDTO req) {
        navShortcutService.batchSaveRecommendSites(userId, categoryId, req);
    }
}
