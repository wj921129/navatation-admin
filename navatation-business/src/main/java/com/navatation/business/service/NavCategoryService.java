package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.math.BigDecimal;
import com.navatation.business.dto.req.nav.CategoryReqDTO;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;
import com.navatation.business.dto.resp.recommend.RecommendCategoryRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavHomeShortcut;
import com.navatation.business.entity.recommend.RecommendCategory;
import com.navatation.business.entity.recommend.RecommendShortcut;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavHomeShortcutMapper;
import com.navatation.business.mapper.RecommendCategoryMapper;
import com.navatation.business.mapper.RecommendShortcutMapper;
import com.navatation.business.mapper.UserMapper;
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

/**
 * NavCategoryService 功能描述
 */
@Service
@RequiredArgsConstructor
public class NavCategoryService {

    private static final Logger log = LoggerFactory.getLogger(NavCategoryService.class);

    private final NavCategoryMapper categoryMapper;
    private final NavHomeShortcutMapper shortcutMapper;
    private final RecommendCategoryMapper recommendCategoryMapper;
    private final RecommendShortcutMapper recommendShortcutMapper;
    private final UserMapper userMapper;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public List<CategoryRespDTO> getCategories(String userId) {
        if (isAdmin(userId)) {
            List<RecommendCategory> categories = recommendCategoryMapper.selectList(
                    new LambdaQueryWrapper<RecommendCategory>()
                            .orderByAsc(RecommendCategory::getSortOrder));

            List<String> categoryIds = categories.stream().map(RecommendCategory::getCategoryId).collect(Collectors.toList());
            Map<String, Long> countMap = categoryIds.isEmpty() ? Map.of() :
                    recommendShortcutMapper.selectList(new LambdaQueryWrapper<RecommendShortcut>().in(RecommendShortcut::getCategoryId, categoryIds))
                            .stream().collect(Collectors.groupingBy(RecommendShortcut::getCategoryId, Collectors.counting()));

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
                shortcutMapper.selectList(new LambdaQueryWrapper<NavHomeShortcut>().in(NavHomeShortcut::getCategoryId, categoryIds))
                        .stream().collect(Collectors.groupingBy(NavHomeShortcut::getCategoryId, Collectors.counting()));

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
            RecommendCategory category = new RecommendCategory();
            category.setCategoryId(IdUtils.genCategoryId());
            category.setName(req.getName());
            category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
            recommendCategoryMapper.insert(category);
            log.info("创建管理员推荐分类成功 userId={} categoryId={} name={}", userId, category.getCategoryId(), category.getName());

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
        category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
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
            RecommendCategory category = recommendCategoryMapper.selectById(categoryId);
            if (category == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            if (req.getName() != null) {
                category.setName(req.getName());
            }
            if (req.getSortOrder() != null) {
                category.setSortOrder(req.getSortOrder());
            }
            recommendCategoryMapper.updateById(category);
            log.info("更新管理员推荐分类成功 userId={} categoryId={}", userId, categoryId);
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
            RecommendCategory category = recommendCategoryMapper.selectById(categoryId);
            if (category == null) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
            recommendShortcutMapper.delete(new LambdaQueryWrapper<RecommendShortcut>()
                    .eq(RecommendShortcut::getCategoryId, categoryId));
            recommendCategoryMapper.deleteById(categoryId);
            log.info("删除管理员推荐分类成功 userId={} categoryId={}", userId, categoryId);
            return;
        }

        NavCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        shortcutMapper.delete(new LambdaQueryWrapper<NavHomeShortcut>()
                .eq(NavHomeShortcut::getCategoryId, categoryId));
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
        List<RecommendShortcut> sites = recommendShortcutMapper.selectList(
                new LambdaQueryWrapper<RecommendShortcut>()
                        .in(RecommendShortcut::getCategoryId, categoryIds)
                        .orderByAsc(RecommendShortcut::getSortOrder));

        Map<String, List<RecommendShortcut>> siteMap = sites.stream()
                .collect(Collectors.groupingBy(RecommendShortcut::getCategoryId));

        return categories.stream().map(cat -> {
            RecommendCategoryRespDTO vo = new RecommendCategoryRespDTO();
            vo.setCategoryId(cat.getCategoryId());
            vo.setCategoryName(cat.getName());
            vo.setCategoryIcon(null); // icon 已移除
            vo.setSortOrder(cat.getSortOrder());
            List<RecommendSiteRespDTO> siteVOs = siteMap.getOrDefault(cat.getCategoryId(), new ArrayList<>()).stream().map(site -> {
                RecommendSiteRespDTO siteVO = new RecommendSiteRespDTO();
                siteVO.setSiteId(site.getShortcutId());
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
        cat.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
        recommendCategoryMapper.insert(cat);

        RecommendCategoryRespDTO vo = new RecommendCategoryRespDTO();
        vo.setCategoryId(cat.getCategoryId());
        vo.setCategoryName(cat.getName());
        vo.setCategoryIcon(null);
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
        recommendShortcutMapper.delete(new LambdaQueryWrapper<RecommendShortcut>().eq(RecommendShortcut::getCategoryId, categoryId));
        recommendCategoryMapper.deleteById(categoryId);
    }
}
