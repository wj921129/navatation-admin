package com.navatation.business.service;

import com.navatation.business.helper.FaviconFetcherHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.UserMapper;

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
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    // --- Category Management ---

    public List<CategoryRespDTO> getCategories(String userId) {
        List<CategoryRespDTO> res = navCategoryService.getCategories(userId);
        if (isAdmin(userId)) {
            try {
                String cacheKey = "navatation:guest_config";
                String cachedStr = (String) redisTemplate.opsForHash().get(cacheKey, "categories");
                String currentStr = objectMapper.writeValueAsString(res);
                if (cachedStr == null || !cachedStr.equals(currentStr)) {
                    redisTemplate.opsForHash().put(cacheKey, "categories", currentStr);
                    org.slf4j.LoggerFactory.getLogger(NavService.class).info("管理员获取分类时发现缓存不一致，已刷新游客分类缓存");
                }
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(NavService.class).warn("刷新游客分类缓存失败", e);
            }
        }
        return res;
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
        List<ShortcutRespDTO> res = navShortcutService.getShortcuts(userId, categoryId);
        if (isAdmin(userId) && categoryId == null) {
            try {
                String cacheKey = "navatation:guest_config";
                String cachedStr = (String) redisTemplate.opsForHash().get(cacheKey, "shortcuts");
                String currentStr = objectMapper.writeValueAsString(res);
                if (cachedStr == null || !cachedStr.equals(currentStr)) {
                    redisTemplate.opsForHash().put(cacheKey, "shortcuts", currentStr);
                    org.slf4j.LoggerFactory.getLogger(NavService.class).info("管理员获取快捷方式时发现缓存不一致，已刷新游客快捷方式缓存");
                }
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(NavService.class).warn("刷新游客快捷方式缓存失败", e);
            }
        }
        return res;
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
