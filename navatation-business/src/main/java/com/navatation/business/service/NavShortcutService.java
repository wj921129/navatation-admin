package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
import com.navatation.business.entity.BaseShortcut;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavShortcut;
import com.navatation.business.entity.recommend.RecommendCategory;
import com.navatation.business.entity.recommend.RecommendSite;
import com.navatation.business.entity.root.RootCategory;
import com.navatation.business.entity.root.RootShortcut;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 快捷方式服务，处理快捷方式分类和数据管理
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
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return rootUserMapper.selectById(userId) != null;
    }

    @SuppressWarnings("rawtypes")
    private BaseMapper getShortcutMapper(boolean isAdmin) {
        return isAdmin ? rootShortcutMapper : shortcutMapper;
    }

    private BaseShortcut createShortcut(boolean isAdmin) {
        return isAdmin ? new RootShortcut() : new NavShortcut();
    }

    /**
     * 根据分类查询快捷方式列表
     */
    public List<ShortcutRespDTO> getShortcuts(String userId, String categoryId) {
        QueryWrapper<Object> wrapper = new QueryWrapper<>()
                .eq("user_id", userId)
                .orderByAsc("sort_order");
        if (StringUtils.hasText(categoryId)) {
            wrapper.eq("category_id", categoryId);
        }
        
        @SuppressWarnings("unchecked")
        List<BaseShortcut> list = (List<BaseShortcut>) getShortcutMapper(isAdmin(userId)).selectList(wrapper);
        
        return list.stream()
                .map(this::toShortcutVO)
                .collect(Collectors.toList());
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
            }
        } else {
            if (admin) {
                RootCategory cat = rootCategoryMapper.selectById(categoryId);
                if (cat == null || !cat.getUserId().equals(userId)) throw new BizException(ResultCode.NOT_FOUND);
            } else {
                NavCategory cat = categoryMapper.selectById(categoryId);
                if (cat == null || !cat.getUserId().equals(userId)) throw new BizException(ResultCode.NOT_FOUND);
            }
        }

        QueryWrapper<Object> wrapper = new QueryWrapper<>().eq("category_id", categoryId);
        @SuppressWarnings("unchecked")
        List<BaseShortcut> existing = (List<BaseShortcut>) getShortcutMapper(admin).selectList(wrapper);
        double maxSort = existing.stream()
                .mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0)
                .max().orElse(0.0);

        List<BatchCreateItemRespDTO> results = new ArrayList<>();
        List<BaseShortcut> saveList = new ArrayList<>();
        for (CreateShortcutItemDTO item : req.getShortcuts()) {
            BaseShortcut shortcut = createShortcut(admin);
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
            saveList.add(shortcut);

            BatchCreateItemRespDTO vo = new BatchCreateItemRespDTO();
            vo.setShortcutId(shortcut.getShortcutId());
            vo.setName(shortcut.getName());
            results.add(vo);
        }
        if (!CollectionUtils.isEmpty(saveList)) {
            Db.saveBatch(saveList);
        }
        log.info("批量创建快捷方式成功 userId={} count={}", userId, results.size());
        return results;
    }

    /**
     * 更新快捷方式配置
     */
    @Transactional
    public ShortcutRespDTO updateShortcut(String userId, String shortcutId, UpdateShortcutReqDTO req) {
        boolean admin = isAdmin(userId);
        BaseShortcut shortcut = (BaseShortcut) getShortcutMapper(admin).selectById(shortcutId);
        
        if (shortcut == null || !shortcut.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        
        shortcut.setName(req.getName());
        shortcut.setUrl(req.getUrl());
        if (req.getIconType() != null) shortcut.setIconType(req.getIconType());
        if (req.getIconValue() != null) shortcut.setIconValue(req.getIconValue());
        if (req.getIconColor() != null) shortcut.setIconColor(req.getIconColor());
        
        Db.updateById(shortcut);
        log.info("更新快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
        return toShortcutVO(shortcut);
    }

    /**
     * 删除快捷方式
     */
    @Transactional
    public void deleteShortcut(String userId, String shortcutId) {
        boolean admin = isAdmin(userId);
        BaseShortcut shortcut = (BaseShortcut) getShortcutMapper(admin).selectById(shortcutId);
        
        if (shortcut == null || !shortcut.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        getShortcutMapper(admin).deleteById(shortcutId);
        log.info("删除快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
    }

    /**
     * 更新快捷方式排序
     */
    @Transactional
    public void sortShortcuts(String userId, SortReqDTO req) {
        boolean admin = isAdmin(userId);
        List<String> ids = req.getItems().stream().map(SortItemDTO::getShortcutId).collect(Collectors.toList());

        QueryWrapper<Object> wrapper = new QueryWrapper<>().in("shortcut_id", ids).eq("user_id", userId);
        @SuppressWarnings("unchecked")
        List<BaseShortcut> shortcuts = (List<BaseShortcut>) getShortcutMapper(admin).selectList(wrapper);
        
        Map<String, BaseShortcut> shortcutMap = shortcuts.stream()
                .collect(Collectors.toMap(BaseShortcut::getShortcutId, Function.identity()));

        List<BaseShortcut> updates = new ArrayList<>();
        for (SortItemDTO item : req.getItems()) {
            BaseShortcut shortcut = shortcutMap.get(item.getShortcutId());
            if (shortcut != null) {
                shortcut.setSortOrder(item.getSortOrder());
                updates.add(shortcut);
            }
        }
        if (!CollectionUtils.isEmpty(updates)) {
            Db.updateBatchById(updates);
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
     * 更新推荐站点信息
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
     * 删除推荐站点
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

    private ShortcutRespDTO toShortcutVO(BaseShortcut s) {
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
