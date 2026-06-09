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
import com.navatation.business.entity.nav.NavShortcut;
import com.navatation.business.entity.recommend.RecommendCategory;
import com.navatation.business.entity.recommend.RecommendSite;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavShortcutMapper;
import com.navatation.business.mapper.RecommendCategoryMapper;
import com.navatation.business.mapper.RecommendSiteMapper;
import com.navatation.business.mapper.RootCategoryMapper;
import com.navatation.business.mapper.RootShortcutMapper;
import com.navatation.business.mapper.RootUserMapper;
import com.navatation.common.BizException;
import com.navatation.common.IdUtils;
import com.navatation.common.NavConstants;
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.navatation.business.entity.root.RootCategory;
import com.navatation.business.entity.root.RootShortcut;

/**
 * NavShortcutService 功能描述
 *
 * @date 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class NavShortcutService {

    private static final Logger log = LoggerFactory.getLogger(NavShortcutService.class);

    private final NavCategoryMapper categoryMapper;
    private final NavShortcutMapper shortcutMapper;
    private final RootCategoryMapper rootCategoryMapper;
    private final RootShortcutMapper rootShortcutMapper;
    private final RecommendCategoryMapper recommendCategoryMapper;
    private final RecommendSiteMapper recommendSiteMapper;
    private final RootUserMapper rootUserMapper;

    private boolean isAdmin(String userId) {
        return rootUserMapper.selectById(userId) != null;
    }

        /**
     * getShortcuts 方法
     */
    public List<ShortcutRespDTO> getShortcuts(String userId, String categoryId) {
        if (isAdmin(userId)) {
            LambdaQueryWrapper<RootShortcut> wrapper = new LambdaQueryWrapper<RootShortcut>()
                    .eq(RootShortcut::getUserId, userId)
                    .orderByAsc(RootShortcut::getSortOrder);
            if (categoryId != null && !categoryId.isEmpty()) {
                wrapper.eq(RootShortcut::getCategoryId, categoryId);
            }
            return rootShortcutMapper.selectList(wrapper).stream()
                    .map(this::toShortcutVO)
                    .collect(Collectors.toList());
        }

        LambdaQueryWrapper<NavShortcut> wrapper = new LambdaQueryWrapper<NavShortcut>()
                .eq(NavShortcut::getUserId, userId)
                .orderByAsc(NavShortcut::getSortOrder);
        if (categoryId != null && !categoryId.isEmpty()) {
            wrapper.eq(NavShortcut::getCategoryId, categoryId);
        }
        return shortcutMapper.selectList(wrapper).stream()
                    .map(this::toShortcutVO)
                    .collect(Collectors.toList());
    }

        /**
     * batchCreate 方法
     */
    @Transactional
    public List<BatchCreateItemRespDTO> batchCreate(String userId, BatchCreateReqDTO req) {
        String categoryId = req.getCategoryId();

        if (isAdmin(userId)) {
            if (categoryId == null || categoryId.isEmpty()) {
                RootCategory defaultCat = rootCategoryMapper.selectOne(
                        new LambdaQueryWrapper<RootCategory>()
                                .eq(RootCategory::getUserId, userId)
                                .eq(RootCategory::getName, NavConstants.DEFAULT_CATEGORY_NAME));
                if (defaultCat == null) {
                    defaultCat = new RootCategory();
                    defaultCat.setCategoryId(IdUtils.genCategoryId());
                    defaultCat.setUserId(userId);
                    defaultCat.setName(NavConstants.DEFAULT_CATEGORY_NAME);
                    defaultCat.setSortOrder(0.0);
                    rootCategoryMapper.insert(defaultCat);
                }
                categoryId = defaultCat.getCategoryId();
            } else {
                RootCategory cat = rootCategoryMapper.selectById(categoryId);
                if (cat == null || !cat.getUserId().equals(userId)) {
                    throw new BizException(ResultCode.NOT_FOUND);
                }
            }

            double maxSort = rootShortcutMapper.selectList(
                    new LambdaQueryWrapper<RootShortcut>()
                            .eq(RootShortcut::getCategoryId, categoryId))
                    .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

            List<BatchCreateItemRespDTO> results = new ArrayList<>();
            for (CreateShortcutItemDTO item : req.getShortcuts()) {
                RootShortcut shortcut = new RootShortcut();
                shortcut.setShortcutId(IdUtils.genShortcutId());
                shortcut.setCategoryId(categoryId);
                shortcut.setUserId(userId);
                shortcut.setName(item.getName());
                shortcut.setUrl(item.getUrl());
                shortcut.setIconType(item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_BUILTIN);
                shortcut.setIconValue(item.getIconValue());
                shortcut.setIconColor(item.getIconColor());
                shortcut.setSortOrder(++maxSort);
                shortcut.setClickCount(0L);
                rootShortcutMapper.insert(shortcut);

                BatchCreateItemRespDTO vo = new BatchCreateItemRespDTO();
                vo.setShortcutId(shortcut.getShortcutId());
                vo.setName(shortcut.getName());
                results.add(vo);
            }
            log.info("批量创建管理员快捷方式成功 userId={} count={}", userId, results.size());
            return results;
        }

        if (categoryId == null || categoryId.isEmpty()) {
            NavCategory defaultCat = categoryMapper.selectOne(
                    new LambdaQueryWrapper<NavCategory>()
                            .eq(NavCategory::getUserId, userId)
                            .eq(NavCategory::getName, NavConstants.DEFAULT_CATEGORY_NAME));
            if (defaultCat == null) {
                defaultCat = new NavCategory();
                defaultCat.setCategoryId(IdUtils.genCategoryId());
                defaultCat.setUserId(userId);
                defaultCat.setName(NavConstants.DEFAULT_CATEGORY_NAME);
                defaultCat.setSortOrder(0.0);
                categoryMapper.insert(defaultCat);
            }
            categoryId = defaultCat.getCategoryId();
        } else {
            NavCategory cat = categoryMapper.selectById(categoryId);
            if (cat == null || !cat.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
        }

        double maxSort = shortcutMapper.selectList(
                new LambdaQueryWrapper<NavShortcut>()
                        .eq(NavShortcut::getCategoryId, categoryId))
                .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

        List<BatchCreateItemRespDTO> results = new ArrayList<>();
        for (CreateShortcutItemDTO item : req.getShortcuts()) {
            NavShortcut shortcut = new NavShortcut();
            shortcut.setShortcutId(IdUtils.genShortcutId());
            shortcut.setCategoryId(categoryId);
            shortcut.setUserId(userId);
            shortcut.setName(item.getName());
            shortcut.setUrl(item.getUrl());
            shortcut.setIconType(item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_BUILTIN);
            shortcut.setIconValue(item.getIconValue());
            shortcut.setIconColor(item.getIconColor());
            shortcut.setSortOrder(++maxSort);
            shortcut.setClickCount(0L);
            shortcutMapper.insert(shortcut);

            BatchCreateItemRespDTO vo = new BatchCreateItemRespDTO();
            vo.setShortcutId(shortcut.getShortcutId());
            vo.setName(shortcut.getName());
            results.add(vo);
        }
        log.info("批量创建快捷方式成功 userId={} count={}", userId, results.size());
        return results;
    }

        /**
     * updateShortcut 方法
     */
    public ShortcutRespDTO updateShortcut(String userId, String shortcutId, UpdateShortcutReqDTO req) {
        if (isAdmin(userId)) {
            RootShortcut shortcut = rootShortcutMapper.selectById(shortcutId);
            if (shortcut == null || !shortcut.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            shortcut.setName(req.getName());
            shortcut.setUrl(req.getUrl());
            if (req.getIconType() != null) {
                shortcut.setIconType(req.getIconType());
            }
            if (req.getIconValue() != null) {
                shortcut.setIconValue(req.getIconValue());
            }
            if (req.getIconColor() != null) {
                shortcut.setIconColor(req.getIconColor());
            }
            rootShortcutMapper.updateById(shortcut);
            log.info("更新管理员快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
            return toShortcutVO(shortcut);
        }

        NavShortcut shortcut = shortcutMapper.selectById(shortcutId);
        if (shortcut == null || !shortcut.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        shortcut.setName(req.getName());
        shortcut.setUrl(req.getUrl());
        if (req.getIconType() != null) {
            shortcut.setIconType(req.getIconType());
        }
        if (req.getIconValue() != null) {
            shortcut.setIconValue(req.getIconValue());
        }
        if (req.getIconColor() != null) {
            shortcut.setIconColor(req.getIconColor());
        }
        shortcutMapper.updateById(shortcut);
        log.info("更新快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
        return toShortcutVO(shortcut);
    }

        /**
     * deleteShortcut 方法
     */
    public void deleteShortcut(String userId, String shortcutId) {
        if (isAdmin(userId)) {
            RootShortcut shortcut = rootShortcutMapper.selectById(shortcutId);
            if (shortcut == null || !shortcut.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            rootShortcutMapper.deleteById(shortcutId);
            log.info("删除管理员快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
            return;
        }

        NavShortcut shortcut = shortcutMapper.selectById(shortcutId);
        if (shortcut == null || !shortcut.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        shortcutMapper.deleteById(shortcutId);
        log.info("删除快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
    }

        /**
     * sortShortcuts 方法
     */
    @Transactional
    public void sortShortcuts(String userId, SortReqDTO req) {
        List<String> ids = req.getItems().stream().map(SortItemDTO::getShortcutId).collect(Collectors.toList());

        if (isAdmin(userId)) {
            Map<String, RootShortcut> shortcutMap = rootShortcutMapper.selectBatchIds(ids).stream()
                    .filter(s -> s.getUserId().equals(userId))
                    .collect(Collectors.toMap(RootShortcut::getShortcutId, Function.identity()));

            for (SortItemDTO item : req.getItems()) {
                RootShortcut shortcut = shortcutMap.get(item.getShortcutId());
                if (shortcut != null) {
                    shortcut.setSortOrder(item.getSortOrder());
                    rootShortcutMapper.updateById(shortcut);
                }
            }
            return;
        }

        Map<String, NavShortcut> shortcutMap = shortcutMapper.selectBatchIds(ids).stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toMap(NavShortcut::getShortcutId, Function.identity()));

        for (SortItemDTO item : req.getItems()) {
            NavShortcut shortcut = shortcutMap.get(item.getShortcutId());
            if (shortcut != null) {
                shortcut.setSortOrder(item.getSortOrder());
                shortcutMapper.updateById(shortcut);
            }
        }
    }

        /**
     * addRecommendSite 方法
     */
    @Transactional
    public RecommendSiteRespDTO addRecommendSite(String userId, RecommendSiteReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendSite site = new RecommendSite();
        site.setSiteId(IdUtils.genRecommendSiteId());
        site.setCategoryId(req.getCategoryId());
        site.setName(req.getName());
        site.setUrl(req.getUrl());
        site.setIconType(req.getIconType());
        site.setIconValue(req.getIconValue());
        site.setIconColor(req.getIconColor());
        site.setSortOrder(0.0);
        recommendSiteMapper.insert(site);

        RecommendSiteRespDTO vo = new RecommendSiteRespDTO();
        vo.setSiteId(site.getSiteId());
        vo.setCategoryId(site.getCategoryId());
        vo.setName(site.getName());
        vo.setUrl(site.getUrl());
        vo.setIconType(site.getIconType());
        vo.setIconValue(site.getIconValue());
        vo.setIconColor(site.getIconColor());

        return vo;
    }

        /**
     * updateRecommendSite 方法
     */
    @Transactional
    public void updateRecommendSite(String userId, String siteId, RecommendSiteReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendSite site = recommendSiteMapper.selectById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        if (req.getCategoryId() != null) site.setCategoryId(req.getCategoryId());
        if (req.getName() != null) site.setName(req.getName());
        if (req.getUrl() != null) site.setUrl(req.getUrl());
        if (req.getIconType() != null) site.setIconType(req.getIconType());
        if (req.getIconValue() != null) site.setIconValue(req.getIconValue());
        if (req.getIconColor() != null) site.setIconColor(req.getIconColor());

        recommendSiteMapper.updateById(site);
    }

        /**
     * deleteRecommendSite 方法
     */
    @Transactional
    public void deleteRecommendSite(String userId, String siteId) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendSite site = recommendSiteMapper.selectById(siteId);
        if (site == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        recommendSiteMapper.deleteById(siteId);
    }

        /**
     * batchSaveRecommendSites 方法
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

        List<RecommendSite> existingSites = recommendSiteMapper.selectList(
                new LambdaQueryWrapper<RecommendSite>()
                        .eq(RecommendSite::getCategoryId, categoryId));

        List<RecommendSiteItemDTO> incomingSites = req.getSites() != null ? req.getSites() : new ArrayList<>();
        Set<String> incomingSiteIds = incomingSites.stream()
                .map(RecommendSiteItemDTO::getSiteId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        List<String> deleteIds = existingSites.stream()
                .map(RecommendSite::getSiteId)
                .filter(id -> !incomingSiteIds.contains(id))
                .collect(Collectors.toList());

        if (!deleteIds.isEmpty()) {
            recommendSiteMapper.deleteBatchIds(deleteIds);
            log.info("管理员批量删除推荐网址成功 categoryId={}, deleteCount={}", categoryId, deleteIds.size());
        }

        Map<String, RecommendSite> existingMap = existingSites.stream()
                .collect(Collectors.toMap(RecommendSite::getSiteId, Function.identity()));

        List<RecommendSite> insertList = new ArrayList<>();
        List<RecommendSite> updateList = new ArrayList<>();

        for (int i = 0; i < incomingSites.size(); i++) {
            RecommendSiteItemDTO item = incomingSites.get(i);
            double currentSortOrder = (double) i + 1;

            if (item.getSiteId() != null && !item.getSiteId().isEmpty() && existingMap.containsKey(item.getSiteId())) {
                RecommendSite dbSite = existingMap.get(item.getSiteId());
                dbSite.setName(item.getName());
                dbSite.setUrl(item.getUrl());
                dbSite.setIconType(item.getIconType());
                dbSite.setIconValue(item.getIconValue());
                dbSite.setIconColor(item.getIconColor());
                dbSite.setSortOrder(currentSortOrder);
                updateList.add(dbSite);
            } else {
                RecommendSite newSite = new RecommendSite();
                newSite.setSiteId(IdUtils.genRecommendSiteId());
                newSite.setCategoryId(categoryId);
                newSite.setName(item.getName());
                newSite.setUrl(item.getUrl());
                newSite.setIconType(item.getIconType() != null ? item.getIconType() : NavConstants.ICON_TYPE_FAVICON);
                newSite.setIconValue(item.getIconValue());
                newSite.setIconColor(item.getIconColor());
                newSite.setSortOrder(currentSortOrder);
                insertList.add(newSite);
            }
        }

        if (!insertList.isEmpty()) {
            Db.saveBatch(insertList);
            log.info("管理员批量新增推荐网址成功 categoryId={}, insertCount={}", categoryId, insertList.size());
        }
        if (!updateList.isEmpty()) {
            Db.updateBatchById(updateList);
            log.info("管理员批量更新推荐网址成功 categoryId={}, updateCount={}", categoryId, updateList.size());
        }
        log.info("管理员批量保存推荐网址成功 categoryId={}, totalCount={}", categoryId, incomingSites.size());
    }

    private ShortcutRespDTO toShortcutVO(NavShortcut s) {
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

    private ShortcutRespDTO toShortcutVO(RootShortcut s) {
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
