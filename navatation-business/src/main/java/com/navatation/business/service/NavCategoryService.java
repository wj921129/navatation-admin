package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.req.CategoryReqDTO;
import com.navatation.business.dto.resp.CategoryRespDTO;
import com.navatation.business.dto.req.RecommendCategoryReqDTO;
import com.navatation.business.dto.resp.RecommendCategoryRespDTO;
import com.navatation.business.dto.resp.RecommendSiteRespDTO;
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
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavCategoryService {

    private static final Logger log = LoggerFactory.getLogger(NavCategoryService.class);

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

    public List<CategoryRespDTO> getCategories(String userId) {
        if (isAdmin(userId)) {
            List<com.navatation.business.entity.root.RootCategory> categories = rootCategoryMapper.selectList(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootCategory>()
                            .eq(com.navatation.business.entity.root.RootCategory::getUserId, userId)
                            .orderByAsc(com.navatation.business.entity.root.RootCategory::getSortOrder));

            List<String> categoryIds = categories.stream().map(com.navatation.business.entity.root.RootCategory::getCategoryId).collect(Collectors.toList());
            Map<String, Long> countMap = categoryIds.isEmpty() ? Map.of() :
                    rootShortcutMapper.selectList(new LambdaQueryWrapper<com.navatation.business.entity.root.RootShortcut>().in(com.navatation.business.entity.root.RootShortcut::getCategoryId, categoryIds))
                            .stream().collect(Collectors.groupingBy(com.navatation.business.entity.root.RootShortcut::getCategoryId, Collectors.counting()));

            return categories.stream().map(c -> {
                CategoryRespDTO vo = new CategoryRespDTO();
                vo.setCategoryId(c.getCategoryId());
                vo.setName(c.getName());
                vo.setSortOrder(c.getSortOrder());
                vo.setShortcutCount(countMap.getOrDefault(c.getCategoryId(), 0L).intValue());
                return vo;
            }).collect(Collectors.toList());
        }

        List<NavCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<NavCategory>()
                        .eq(NavCategory::getUserId, userId)
                        .orderByAsc(NavCategory::getSortOrder));

        List<String> categoryIds = categories.stream().map(NavCategory::getCategoryId).collect(Collectors.toList());
        Map<String, Long> countMap = categoryIds.isEmpty() ? Map.of() :
                shortcutMapper.selectList(new LambdaQueryWrapper<NavShortcut>().in(NavShortcut::getCategoryId, categoryIds))
                        .stream().collect(Collectors.groupingBy(NavShortcut::getCategoryId, Collectors.counting()));

        return categories.stream().map(c -> {
            CategoryRespDTO vo = new CategoryRespDTO();
            vo.setCategoryId(c.getCategoryId());
            vo.setName(c.getName());
            vo.setSortOrder(c.getSortOrder());
            vo.setShortcutCount(countMap.getOrDefault(c.getCategoryId(), 0L).intValue());
            return vo;
        }).collect(Collectors.toList());
    }

    public CategoryRespDTO createCategory(String userId, CategoryReqDTO req) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootCategory category = new com.navatation.business.entity.root.RootCategory();
            category.setCategoryId(IdUtils.genCategoryId());
            category.setUserId(userId);
            category.setName(req.getName());
            category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
            rootCategoryMapper.insert(category);
            log.info("创建管理员分类成功 userId={} categoryId={} name={}", userId, category.getCategoryId(), category.getName());

            CategoryRespDTO vo = new CategoryRespDTO();
            vo.setCategoryId(category.getCategoryId());
            vo.setName(category.getName());
            vo.setSortOrder(category.getSortOrder());
            vo.setShortcutCount(0);
            return vo;
        }

        NavCategory category = new NavCategory();
        category.setCategoryId(IdUtils.genCategoryId());
        category.setUserId(userId);
        category.setName(req.getName());
        category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
        categoryMapper.insert(category);
        log.info("创建分类成功 userId={} categoryId={} name={}", userId, category.getCategoryId(), category.getName());

        CategoryRespDTO vo = new CategoryRespDTO();
        vo.setCategoryId(category.getCategoryId());
        vo.setName(category.getName());
        vo.setSortOrder(category.getSortOrder());
        vo.setShortcutCount(0);
        return vo;
    }

    public void updateCategory(String userId, String categoryId, CategoryReqDTO req) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootCategory category = rootCategoryMapper.selectById(categoryId);
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            if (req.getName() != null) {
                category.setName(req.getName());
            }
            if (req.getSortOrder() != null) {
                category.setSortOrder(req.getSortOrder());
            }
            rootCategoryMapper.updateById(category);
            log.info("更新管理员分类成功 userId={} categoryId={}", userId, categoryId);
            return;
        }

        NavCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        if (req.getName() != null) {
            category.setName(req.getName());
        }
        if (req.getSortOrder() != null) {
            category.setSortOrder(req.getSortOrder());
        }
        categoryMapper.updateById(category);
        log.info("更新分类成功 userId={} categoryId={}", userId, categoryId);
    }

    @Transactional
    public void deleteCategory(String userId, String categoryId) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootCategory category = rootCategoryMapper.selectById(categoryId);
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            rootShortcutMapper.delete(new LambdaQueryWrapper<com.navatation.business.entity.root.RootShortcut>()
                    .eq(com.navatation.business.entity.root.RootShortcut::getCategoryId, categoryId));
            rootCategoryMapper.deleteById(categoryId);
            log.info("删除管理员分类成功 userId={} categoryId={}", userId, categoryId);
            return;
        }

        NavCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        shortcutMapper.delete(new LambdaQueryWrapper<NavShortcut>()
                .eq(NavShortcut::getCategoryId, categoryId));
        categoryMapper.deleteById(categoryId);
        log.info("删除分类成功 userId={} categoryId={}", userId, categoryId);
    }

    public List<RecommendCategoryRespDTO> getRecommended() {
        List<RecommendCategory> categories = recommendCategoryMapper.selectList(
                new LambdaQueryWrapper<RecommendCategory>().orderByAsc(RecommendCategory::getSortOrder));
        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> categoryIds = categories.stream().map(RecommendCategory::getCategoryId).collect(Collectors.toList());
        List<RecommendSite> sites = recommendSiteMapper.selectList(
                new LambdaQueryWrapper<RecommendSite>()
                        .in(RecommendSite::getCategoryId, categoryIds)
                        .orderByAsc(RecommendSite::getSortOrder));

        Map<String, List<RecommendSite>> siteMap = sites.stream()
                .collect(Collectors.groupingBy(RecommendSite::getCategoryId));

        return categories.stream().map(cat -> {
            RecommendCategoryRespDTO vo = new RecommendCategoryRespDTO();
            vo.setCategoryId(cat.getCategoryId());
            vo.setCategoryName(cat.getName());
            vo.setCategoryIcon(cat.getIcon());
            vo.setSortOrder(cat.getSortOrder());
            List<RecommendSiteRespDTO> siteVOs = siteMap.getOrDefault(cat.getCategoryId(), new ArrayList<>()).stream().map(site -> {
                RecommendSiteRespDTO siteVO = new RecommendSiteRespDTO();
                siteVO.setSiteId(site.getSiteId());
                siteVO.setCategoryId(site.getCategoryId());
                siteVO.setName(site.getName());
                siteVO.setUrl(site.getUrl());
                siteVO.setIconType(site.getIconType());
                siteVO.setIconValue(site.getIconValue());
                siteVO.setIconColor(site.getIconColor());

                return siteVO;
            }).collect(Collectors.toList());
            vo.setSites(siteVOs);
            return vo;
        }).collect(Collectors.toList());
    }

    @Transactional
    public RecommendCategoryRespDTO addRecommendCategory(String userId, RecommendCategoryReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendCategory cat = new RecommendCategory();
        cat.setCategoryId(IdUtils.genRecommendCategoryId());
        cat.setName(req.getName());
        cat.setIcon(req.getIcon());
        cat.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
        recommendCategoryMapper.insert(cat);

        RecommendCategoryRespDTO vo = new RecommendCategoryRespDTO();
        vo.setCategoryId(cat.getCategoryId());
        vo.setCategoryName(cat.getName());
        vo.setCategoryIcon(cat.getIcon());
        vo.setSortOrder(cat.getSortOrder());
        vo.setSites(new ArrayList<>());
        return vo;
    }

    @Transactional
    public void updateRecommendCategory(String userId, String categoryId, RecommendCategoryReqDTO req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendCategory cat = recommendCategoryMapper.selectById(categoryId);
        if (cat == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        if (req.getName() != null) cat.setName(req.getName());
        if (req.getIcon() != null) cat.setIcon(req.getIcon());
        if (req.getSortOrder() != null) cat.setSortOrder(req.getSortOrder());
        recommendCategoryMapper.updateById(cat);
    }

    @Transactional
    public void deleteRecommendCategory(String userId, String categoryId) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendCategory cat = recommendCategoryMapper.selectById(categoryId);
        if (cat == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        recommendSiteMapper.delete(new LambdaQueryWrapper<RecommendSite>().eq(RecommendSite::getCategoryId, categoryId));
        recommendCategoryMapper.deleteById(categoryId);
    }
}
