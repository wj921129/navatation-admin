package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.navatation.business.dto.resp.nav.BatchCreateItemRespDTO;
import com.navatation.business.dto.req.nav.BatchCreateReqDTO;
import com.navatation.business.dto.req.recommend.BatchRecommendSiteSaveReqDTO;
import com.navatation.business.dto.req.nav.CreateShortcutItemDTO;
import com.navatation.business.dto.req.recommend.RecommendSiteItemDTO;
import com.navatation.business.dto.req.recommend.RecommendSiteReqDTO;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;
import com.navatation.business.dto.req.nav.SortItemDTO;
import com.navatation.business.dto.req.nav.SortReqDTO;
import com.navatation.business.dto.req.nav.UpdateShortcutReqDTO;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavHomeShortcut;
import com.navatation.business.entity.recommend.RecommendCategory;
import com.navatation.business.entity.recommend.RecommendShortcut;
import com.navatation.business.entity.user.User;
import com.navatation.business.helper.FaviconFetcherHelper;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavHomeShortcutMapper;
import com.navatation.business.mapper.RecommendCategoryMapper;
import com.navatation.business.mapper.RecommendShortcutMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.common.BizException;
import com.navatation.common.IdUtils;
import com.navatation.common.NavConstants;
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 快捷方式服务，处理快捷方式分类和数据管理
 */
@Service
@RequiredArgsConstructor
public class NavShortcutService {

    private static final Logger log = LoggerFactory.getLogger(NavShortcutService.class);

    private final NavCategoryMapper categoryMapper;
    private final NavHomeShortcutMapper shortcutMapper;
    private final RecommendCategoryMapper recommendCategoryMapper;
    private final RecommendShortcutMapper recommendShortcutMapper;
    private final UserMapper userMapper;
    private final FaviconFetcherHelper faviconFetcherHelper;
    private final NavShortcutIconAsyncService navShortcutIconAsyncService;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * 根据分类查询快捷方式列表
     */
    public List<ShortcutRespDTO> getShortcuts(String userId, String categoryId) {
        if (isAdmin(userId)) {
            LambdaQueryWrapper<RecommendShortcut> wrapper = new LambdaQueryWrapper<RecommendShortcut>()
                    .orderByAsc(RecommendShortcut::getSortOrder);
            if (StringUtils.hasText(categoryId)) {
                wrapper.eq(RecommendShortcut::getCategoryId, categoryId);
            }
            List<RecommendShortcut> list = recommendShortcutMapper.selectList(wrapper);
            return list.stream().map(this::toShortcutVO).collect(Collectors.toList());
        } else {
            LambdaQueryWrapper<NavHomeShortcut> wrapper = new LambdaQueryWrapper<NavHomeShortcut>()
                    .eq(NavHomeShortcut::getUserId, userId)
                    .orderByAsc(NavHomeShortcut::getSortOrder);
            if (StringUtils.hasText(categoryId)) {
                wrapper.eq(NavHomeShortcut::getCategoryId, categoryId);
            } else {
                wrapper.isNotNull(NavHomeShortcut::getCategoryId);
            }
            List<NavHomeShortcut> list = shortcutMapper.selectList(wrapper);
            return list.stream().map(this::toShortcutVO).collect(Collectors.toList());
        }
    }

    /**
     * 批量创建快捷方式
     */
    @Transactional
    public List<BatchCreateItemRespDTO> batchCreate(String userId, BatchCreateReqDTO req) {
        String categoryId = req.getCategoryId();
        boolean admin = isAdmin(userId);

        if (!StringUtils.hasText(categoryId)) {
            if (admin) {
                RecommendCategory defaultCat = recommendCategoryMapper.selectOne(
                        new LambdaQueryWrapper<RecommendCategory>()
                                .eq(RecommendCategory::getName, NavConstants.DEFAULT_CATEGORY_NAME).last("LIMIT 1"));
                if (defaultCat == null) {
                    defaultCat = new RecommendCategory();
                    defaultCat.setCategoryId(IdUtils.genCategoryId());
                    defaultCat.setName(NavConstants.DEFAULT_CATEGORY_NAME);
                    defaultCat.setSortOrder(BigDecimal.ZERO);
                    recommendCategoryMapper.insert(defaultCat);
                }
                categoryId = defaultCat.getCategoryId();
            } else {
                NavCategory defaultCat = categoryMapper.selectOne(
                        new LambdaQueryWrapper<NavCategory>()
                                .eq(NavCategory::getUserId, userId)
                                .eq(NavCategory::getName, NavConstants.DEFAULT_CATEGORY_NAME));
                if (defaultCat == null) {
                    defaultCat = new NavCategory();
                    defaultCat.setCategoryId(IdUtils.genCategoryId());
                    defaultCat.setUserId(userId);
                    defaultCat.setName(NavConstants.DEFAULT_CATEGORY_NAME);
                    defaultCat.setSortOrder(BigDecimal.ZERO);
                    categoryMapper.insert(defaultCat);
                }
                categoryId = defaultCat.getCategoryId();
            }
        } else {
            if (admin) {
                RecommendCategory cat = recommendCategoryMapper.selectById(categoryId);
                if (cat == null) throw new BizException(ResultCode.NOT_FOUND);
            } else {
                NavCategory cat = categoryMapper.selectById(categoryId);
                if (cat == null || !cat.getUserId().equals(userId)) throw new BizException(ResultCode.NOT_FOUND);
            }
        }

        List<BatchCreateItemRespDTO> results = new ArrayList<>();

        if (admin) {
            List<RecommendShortcut> existing = recommendShortcutMapper.selectList(
                    new LambdaQueryWrapper<RecommendShortcut>().eq(RecommendShortcut::getCategoryId, categoryId));
            BigDecimal maxSort = existing.stream()
                    .map(item -> item.getSortOrder() != null ? item.getSortOrder() : BigDecimal.ZERO)
                    .max(java.util.Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
            
            List<RecommendShortcut> saveList = new ArrayList<>();
            for (CreateShortcutItemDTO item : req.getShortcuts()) {
                RecommendShortcut shortcut = new RecommendShortcut();
                shortcut.setShortcutId(IdUtils.genShortcutId());
                shortcut.setCategoryId(categoryId);
                shortcut.setName(item.getName());
                shortcut.setUrl(item.getUrl());
                shortcut.setIconType(item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_BUILTIN);
                shortcut.setIconValue(item.getIconValue());
                shortcut.setIconColor(item.getIconColor());
                maxSort = maxSort.add(BigDecimal.ONE);
                shortcut.setSortOrder(maxSort);
                saveList.add(shortcut);

                BatchCreateItemRespDTO vo = new BatchCreateItemRespDTO();
                vo.setShortcutId(shortcut.getShortcutId());
                vo.setName(shortcut.getName());
                results.add(vo);
            }
            if (!saveList.isEmpty()) {
                Db.saveBatch(saveList);
            }
        } else {
            List<NavHomeShortcut> existing = shortcutMapper.selectList(
                    new LambdaQueryWrapper<NavHomeShortcut>().eq(NavHomeShortcut::getCategoryId, categoryId));
            BigDecimal maxSort = existing.stream()
                    .map(item -> item.getSortOrder() != null ? item.getSortOrder() : BigDecimal.ZERO)
                    .max(java.util.Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
            
            List<NavHomeShortcut> saveList = new ArrayList<>();
            for (CreateShortcutItemDTO item : req.getShortcuts()) {
                NavHomeShortcut shortcut = new NavHomeShortcut();
                shortcut.setShortcutId(IdUtils.genShortcutId());
                shortcut.setCategoryId(categoryId);
                shortcut.setUserId(userId);
                shortcut.setName(item.getName());
                shortcut.setUrl(item.getUrl());
                shortcut.setIconType(item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_BUILTIN);
                shortcut.setIconValue(item.getIconValue());
                shortcut.setIconColor(item.getIconColor());
                maxSort = maxSort.add(BigDecimal.ONE);
                shortcut.setSortOrder(maxSort);
                shortcut.setClickCount(0L);
                saveList.add(shortcut);

                BatchCreateItemRespDTO vo = new BatchCreateItemRespDTO();
                vo.setShortcutId(shortcut.getShortcutId());
                vo.setName(shortcut.getName());
                results.add(vo);
            }
            if (!saveList.isEmpty()) {
                Db.saveBatch(saveList);
            }
        }

        log.info("批量创建快捷方式成功 userId={} count={}", userId, results.size());
        return results;
    }

    /**
     * 更新快捷方式配置
     */
    @Transactional
    public ShortcutRespDTO updateShortcut(String userId, String shortcutId, UpdateShortcutReqDTO req) {
        if (isAdmin(userId)) {
            RecommendShortcut shortcut = recommendShortcutMapper.selectById(shortcutId);
            if (shortcut == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            if (req.getName() != null) shortcut.setName(req.getName());
            if (req.getUrl() != null) shortcut.setUrl(req.getUrl());
            if (req.getIconType() != null) shortcut.setIconType(req.getIconType());
            if (req.getIconValue() != null) shortcut.setIconValue(localizeIcon(
                    req.getIconType() != null ? req.getIconType() : shortcut.getIconType(),
                    req.getIconValue(),
                    req.getUrl() != null ? req.getUrl() : shortcut.getUrl()));
            if (req.getIconColor() != null) shortcut.setIconColor(req.getIconColor());
            
            recommendShortcutMapper.updateById(shortcut);
            return toShortcutVO(shortcut);
        } else {
            NavHomeShortcut shortcut = shortcutMapper.selectById(shortcutId);
            if (shortcut == null || !shortcut.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            if (req.getName() != null) shortcut.setName(req.getName());
            if (req.getUrl() != null) shortcut.setUrl(req.getUrl());
            if (req.getIconType() != null) shortcut.setIconType(req.getIconType());
            if (req.getIconValue() != null) shortcut.setIconValue(localizeIcon(
                    req.getIconType() != null ? req.getIconType() : shortcut.getIconType(),
                    req.getIconValue(),
                    req.getUrl() != null ? req.getUrl() : shortcut.getUrl()));
            if (req.getIconColor() != null) shortcut.setIconColor(req.getIconColor());
            
            shortcutMapper.updateById(shortcut);
            return toShortcutVO(shortcut);
        }
    }

    /**
     * 删除快捷方式
     */
    @Transactional
    public void deleteShortcut(String userId, String shortcutId) {
        if (isAdmin(userId)) {
            RecommendShortcut shortcut = recommendShortcutMapper.selectById(shortcutId);
            if (shortcut == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            recommendShortcutMapper.deleteById(shortcutId);
        } else {
            NavHomeShortcut shortcut = shortcutMapper.selectById(shortcutId);
            if (shortcut == null || !shortcut.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            shortcutMapper.deleteById(shortcutId);
        }
    }

    /**
     * 更新快捷方式排序
     */
    @Transactional
    public void sortShortcuts(String userId, SortReqDTO req) {
        List<String> ids = req.getItems().stream().map(SortItemDTO::getShortcutId).collect(Collectors.toList());

        if (isAdmin(userId)) {
            List<RecommendShortcut> shortcuts = recommendShortcutMapper.selectList(
                    new LambdaQueryWrapper<RecommendShortcut>().in(RecommendShortcut::getShortcutId, ids));
            Map<String, RecommendShortcut> map = shortcuts.stream().collect(Collectors.toMap(RecommendShortcut::getShortcutId, Function.identity()));
            List<RecommendShortcut> updates = new ArrayList<>();
            for (SortItemDTO item : req.getItems()) {
                RecommendShortcut sc = map.get(item.getShortcutId());
                if (sc != null) {
                    sc.setSortOrder(item.getSortOrder());
                    updates.add(sc);
                }
            }
            if (!updates.isEmpty()) Db.updateBatchById(updates);
        } else {
            List<NavHomeShortcut> shortcuts = shortcutMapper.selectList(
                    new LambdaQueryWrapper<NavHomeShortcut>().in(NavHomeShortcut::getShortcutId, ids).eq(NavHomeShortcut::getUserId, userId));
            Map<String, NavHomeShortcut> map = shortcuts.stream().collect(Collectors.toMap(NavHomeShortcut::getShortcutId, Function.identity()));
            List<NavHomeShortcut> updates = new ArrayList<>();
            for (SortItemDTO item : req.getItems()) {
                NavHomeShortcut sc = map.get(item.getShortcutId());
                if (sc != null) {
                    sc.setSortOrder(item.getSortOrder());
                    updates.add(sc);
                }
            }
            if (!updates.isEmpty()) Db.updateBatchById(updates);
        }
    }

    /**
     * 新增推荐站点
     */
    @Transactional
    public RecommendSiteRespDTO addRecommendSite(String userId, RecommendSiteReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendShortcut site = new RecommendShortcut();
        site.setShortcutId(IdUtils.genRecommendSiteId());
        site.setCategoryId(req.getCategoryId());
        site.setName(req.getName());
        site.setUrl(req.getUrl());
        site.setIconType(req.getIconType());
        site.setIconValue(localizeIcon(req.getIconType() != null ? req.getIconType() : NavConstants.ICON_TYPE_FAVICON, req.getIconValue(), req.getUrl()));
        site.setIconColor(req.getIconColor());
        site.setSortOrder(BigDecimal.ZERO);
        recommendShortcutMapper.insert(site);

        RecommendSiteRespDTO vo = new RecommendSiteRespDTO();
        vo.setSiteId(site.getShortcutId());
        vo.setCategoryId(site.getCategoryId());
        vo.setName(site.getName());
        vo.setUrl(site.getUrl());
        vo.setIconType(site.getIconType());
        vo.setIconValue(site.getIconValue());
        vo.setIconColor(site.getIconColor());

        return vo;
    }

    /**
     * 更新推荐站点信息
     */
    @Transactional
    public void updateRecommendSite(String userId, String siteId, RecommendSiteReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendShortcut site = recommendShortcutMapper.selectById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        if (req.getCategoryId() != null) site.setCategoryId(req.getCategoryId());
        if (req.getName() != null) site.setName(req.getName());
        if (req.getUrl() != null) site.setUrl(req.getUrl());
        if (req.getIconType() != null) site.setIconType(req.getIconType());
        if (req.getIconValue() != null) site.setIconValue(localizeIcon(
                req.getIconType() != null ? req.getIconType() : site.getIconType(),
                req.getIconValue(),
                req.getUrl() != null ? req.getUrl() : site.getUrl()));
        if (req.getIconColor() != null) site.setIconColor(req.getIconColor());

        recommendShortcutMapper.updateById(site);
    }

    /**
     * 删除推荐站点
     */
    @Transactional
    public void deleteRecommendSite(String userId, String siteId) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendShortcut site = recommendShortcutMapper.selectById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        recommendShortcutMapper.deleteById(siteId);
    }

    /**
     * 批量保存指定分类下的推荐网址
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveRecommendSites(String userId, String categoryId, BatchRecommendSiteSaveReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }

        RecommendCategory category = recommendCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BizException(ResultCode.NOT_FOUND.getCode(), "推荐分类不存在");
        }

        List<RecommendShortcut> existingSites = recommendShortcutMapper.selectList(
                new LambdaQueryWrapper<RecommendShortcut>()
                        .eq(RecommendShortcut::getCategoryId, categoryId));

        List<RecommendSiteItemDTO> incomingSites = req.getSites() != null ? req.getSites() : new ArrayList<>();
        Set<String> incomingSiteIds = incomingSites.stream()
                .map(RecommendSiteItemDTO::getSiteId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        List<String> deleteIds = existingSites.stream()
                .map(RecommendShortcut::getShortcutId)
                .filter(id -> !incomingSiteIds.contains(id))
                .collect(Collectors.toList());

        if (!deleteIds.isEmpty()) {
            recommendShortcutMapper.deleteBatchIds(deleteIds);
        }

        Map<String, RecommendShortcut> existingMap = existingSites.stream()
                .collect(Collectors.toMap(RecommendShortcut::getShortcutId, Function.identity()));

        List<RecommendShortcut> insertList = new ArrayList<>();
        List<RecommendShortcut> updateList = new ArrayList<>();
        List<RecommendShortcut> pendingDownloadSites = new ArrayList<>();

        for (int i = 0; i < incomingSites.size(); i++) {
            RecommendSiteItemDTO item = incomingSites.get(i);
            BigDecimal currentSortOrder = BigDecimal.valueOf(i + 1);
            
            String iconType = item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_FAVICON;
            String iconValue = item.getIconValue();

            if (item.getSiteId() != null && !item.getSiteId().isEmpty() && existingMap.containsKey(item.getSiteId())) {
                RecommendShortcut dbSite = existingMap.get(item.getSiteId());
                dbSite.setName(item.getName());
                dbSite.setUrl(item.getUrl());
                dbSite.setIconType(iconType);
                dbSite.setIconValue(iconValue);
                dbSite.setIconColor(item.getIconColor());
                dbSite.setSortOrder(currentSortOrder);
                updateList.add(dbSite);
                if (needsDownload(iconType, iconValue)) {
                    pendingDownloadSites.add(dbSite);
                }
            } else {
                RecommendShortcut newSite = new RecommendShortcut();
                newSite.setShortcutId(IdUtils.genRecommendSiteId());
                newSite.setCategoryId(categoryId);
                newSite.setName(item.getName());
                newSite.setUrl(item.getUrl());
                newSite.setIconType(iconType);
                newSite.setIconValue(iconValue);
                newSite.setIconColor(item.getIconColor());
                newSite.setSortOrder(currentSortOrder);
                insertList.add(newSite);
                if (needsDownload(iconType, iconValue)) {
                    pendingDownloadSites.add(newSite);
                }
            }
        }

        if (!insertList.isEmpty()) Db.saveBatch(insertList);
        if (!updateList.isEmpty()) Db.updateBatchById(updateList);
        
        if (!pendingDownloadSites.isEmpty()) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        navShortcutIconAsyncService.asyncBatchDownloadAndSaveIcons(pendingDownloadSites);
                    }
                });
            } else {
                navShortcutIconAsyncService.asyncBatchDownloadAndSaveIcons(pendingDownloadSites);
            }
        }
    }

    private boolean needsDownload(String iconType, String iconValue) {
        if (!"FAVICON".equals(iconType) || iconValue == null || iconValue.isEmpty()) {
            return false;
        }
        if (iconValue.startsWith("/uploads/")) {
            return false;
        }
        return iconValue.startsWith("http://") || iconValue.startsWith("https://");
    }

    /**
     * 如果 iconType 为 FAVICON 且 iconValue 是外部 URL，则下载到本地并返回本地路径。
     */
    private String localizeIcon(String iconType, String iconValue, String shortcutUrl) {
        if (!"FAVICON".equals(iconType) || iconValue == null || iconValue.isEmpty()) {
            return iconValue;
        }
        if (iconValue.startsWith("/uploads/")) {
            return iconValue;
        }
        if (!iconValue.startsWith("http://") && !iconValue.startsWith("https://")) {
            return iconValue;
        }
        try {
            String host = null;
            if (shortcutUrl != null && !shortcutUrl.isEmpty()) {
                if (!shortcutUrl.startsWith("http://") && !shortcutUrl.startsWith("https://")) {
                    shortcutUrl = "http://" + shortcutUrl;
                }
                host = new java.net.URI(shortcutUrl).getHost();
            }
            if (host == null) {
                host = new java.net.URI(iconValue).getHost();
            }
            if (host != null) {
                return faviconFetcherHelper.downloadToLocal(iconValue, host);
            }
        } catch (Exception e) {
            log.warn("本地图标化失败 url: {}, error: {}", iconValue, e.getMessage());
        }
        return iconValue;
    }

    private ShortcutRespDTO toShortcutVO(NavHomeShortcut s) {
        ShortcutRespDTO vo = new ShortcutRespDTO();
        vo.setShortcutId(s.getShortcutId());
        vo.setCategoryId(s.getCategoryId());
        vo.setName(s.getName());
        vo.setUrl(s.getUrl());
        vo.setIconType(s.getIconType());
        vo.setIconValue(s.getIconValue());
        vo.setIconColor(s.getIconColor());
        vo.setSortOrder(s.getSortOrder());
        vo.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
        return vo;
    }

    private ShortcutRespDTO toShortcutVO(RecommendShortcut s) {
        ShortcutRespDTO vo = new ShortcutRespDTO();
        vo.setShortcutId(s.getShortcutId());
        vo.setCategoryId(s.getCategoryId());
        vo.setName(s.getName());
        vo.setUrl(s.getUrl());
        vo.setIconType(s.getIconType());
        vo.setIconValue(s.getIconValue());
        vo.setIconColor(s.getIconColor());
        vo.setSortOrder(s.getSortOrder());
        vo.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
        return vo;
    }
}
