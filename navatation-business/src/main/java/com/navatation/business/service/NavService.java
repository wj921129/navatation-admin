package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.BatchCreateItemVO;
import com.navatation.business.dto.BatchCreateRequest;
import com.navatation.business.dto.CategoryRequest;
import com.navatation.business.dto.CategoryVO;
import com.navatation.business.dto.CreateShortcutItem;
import com.navatation.business.dto.FaviconVO;
import com.navatation.business.dto.IconUploadVO;
import com.navatation.business.dto.RecommendCategoryVO;
import com.navatation.business.dto.RecommendSiteVO;
import com.navatation.business.dto.ShortcutVO;
import com.navatation.business.dto.SortItem;
import com.navatation.business.dto.SortRequest;
import com.navatation.business.dto.UpdateShortcutRequest;
import com.navatation.business.entity.NavCategory;
import com.navatation.business.entity.NavShortcut;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavShortcutMapper;
import com.navatation.business.mapper.RecommendCategoryMapper;
import com.navatation.business.mapper.RecommendSiteMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.entity.RecommendCategory;
import com.navatation.business.entity.RecommendSite;
import com.navatation.business.entity.User;
import com.navatation.business.dto.RecommendCategoryRequest;
import com.navatation.business.dto.RecommendSiteRequest;
import com.navatation.common.BizException;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import com.navatation.common.IdUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航服务，处理分类和快捷方式的CRUD、排序、Favicon抓取及推荐站点管�?
 */
@Service
@RequiredArgsConstructor
public class NavService {

    private static final Logger log = LoggerFactory.getLogger(NavService.class);

    private static final String ICON_TYPE_BUILTIN = "BUILTIN";
    private static final String DEFAULT_CATEGORY_NAME = "常用";

    /** 允许上传的图�?MIME 类型白名�?*/
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp",
            "image/x-icon", "image/vnd.microsoft.icon", "image/svg+xml");

    /** 图标文件大小上限�?00KB */
    private static final long MAX_ICON_SIZE = 200 * 1024;

    /** 每小时每用户最大上传次�?*/
    private static final int MAX_UPLOADS_PER_HOUR = 30;

    private final NavCategoryMapper categoryMapper;
    private final NavShortcutMapper shortcutMapper;
    private final RecommendCategoryMapper recommendCategoryMapper;
    private final RecommendSiteMapper recommendSiteMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    @Value("${app.upload.icon-path}")
    private String iconPath;

    /**
     * 查询用户所有分类及各自快捷方式数量
     * @param userId 用户ID
     * @return 分类列表
     */
    public List<CategoryVO> getCategories(String userId) {

        List<NavCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<NavCategory>()
                        .eq(NavCategory::getUserId, userId)
                        .orderByAsc(NavCategory::getSortOrder));

        // 批量查询各分类下的快捷方式数�?
        List<String> categoryIds = categories.stream().map(NavCategory::getCategoryId).collect(Collectors.toList());
        Map<String, Long> countMap = categoryIds.isEmpty() ? Map.of() :
                shortcutMapper.selectList(new LambdaQueryWrapper<NavShortcut>().in(NavShortcut::getCategoryId, categoryIds))
                        .stream().collect(Collectors.groupingBy(NavShortcut::getCategoryId, Collectors.counting()));

        return categories.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setCategoryId(c.getCategoryId());
            vo.setName(c.getName());
            vo.setSortOrder(c.getSortOrder());
            vo.setShortcutCount(countMap.getOrDefault(c.getCategoryId(), 0L).intValue());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 创建分类
     * @param userId 用户ID
     * @param req 分类请求
     * @return 创建的分�?
     */
    public CategoryVO createCategory(String userId, CategoryRequest req) {

        NavCategory category = new NavCategory();
        category.setCategoryId(IdUtils.genCategoryId());
        category.setUserId(userId);
        category.setName(req.getName());
        category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
        categoryMapper.insert(category);
        log.info("创建分类成功 userId={} categoryId={} name={}", userId, category.getCategoryId(), category.getName());

        CategoryVO vo = new CategoryVO();
        vo.setCategoryId(category.getCategoryId());
        vo.setName(category.getName());
        vo.setSortOrder(category.getSortOrder());
        vo.setShortcutCount(0);
        return vo;
    }

    /**
     * 更新分类名称或排�?
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param req 分类请求
     */
    public void updateCategory(String userId, String categoryId, CategoryRequest req) {

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

    /**
     * 删除分类及其下所有快捷方�?
     * @param userId 用户ID
     * @param categoryId 分类ID
     */
    @Transactional
    public void deleteCategory(String userId, String categoryId) {

        NavCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        // 删除该分类下的所有快捷方�?
        shortcutMapper.delete(new LambdaQueryWrapper<NavShortcut>()
                .eq(NavShortcut::getCategoryId, categoryId));
        categoryMapper.deleteById(categoryId);
        log.info("删除分类成功 userId={} categoryId={}", userId, categoryId);
    }

    /**
     * 查询用户快捷方式列表，可按分类筛�?
     * @param userId 用户ID
     * @param categoryId 分类ID（可为null表示全部�?
     * @return 快捷方式列表
     */
    public List<ShortcutVO> getShortcuts(String userId, String categoryId) {

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
     * 批量创建快捷方式
     * @param userId 用户ID
     * @param req 批量创建请求
     * @return 创建结果列表
     */
    @Transactional
    public List<BatchCreateItemVO> batchCreate(String userId, BatchCreateRequest req) {

        // 确保默认分类存在
        String categoryId = req.getCategoryId();
        if (categoryId == null || categoryId.isEmpty()) {
            NavCategory defaultCat = categoryMapper.selectOne(
                    new LambdaQueryWrapper<NavCategory>()
                            .eq(NavCategory::getUserId, userId)
                            .eq(NavCategory::getName, DEFAULT_CATEGORY_NAME));
            if (defaultCat == null) {
                defaultCat = new NavCategory();
                defaultCat.setCategoryId(IdUtils.genCategoryId());
                defaultCat.setUserId(userId);
                defaultCat.setName(DEFAULT_CATEGORY_NAME);
                defaultCat.setSortOrder(0.0);
                categoryMapper.insert(defaultCat);
            }
            categoryId = defaultCat.getCategoryId();
        } else {
            // 校验 categoryId 是否属于当前用户，防止水平越�?
            NavCategory cat = categoryMapper.selectById(categoryId);
            if (cat == null || !cat.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
        }

        double maxSort = shortcutMapper.selectList(
                new LambdaQueryWrapper<NavShortcut>()
                        .eq(NavShortcut::getCategoryId, categoryId))
                .stream().mapToDouble(item -> item.getSortOrder() != null ? item.getSortOrder() : 0.0).max().orElse(0.0);

        List<BatchCreateItemVO> results = new ArrayList<>();
        for (CreateShortcutItem item : req.getShortcuts()) {
            NavShortcut shortcut = new NavShortcut();
            shortcut.setShortcutId(IdUtils.genShortcutId());
            shortcut.setCategoryId(categoryId);
            shortcut.setUserId(userId);
            shortcut.setName(item.getName());
            shortcut.setUrl(item.getUrl());
            shortcut.setIconType(item.getIconType() != null ? item.getIconType() : ICON_TYPE_BUILTIN);
            shortcut.setIconValue(item.getIconValue());
            shortcut.setIconColor(item.getIconColor());
            shortcut.setSortOrder(++maxSort);
            shortcut.setClickCount(0L);
            shortcutMapper.insert(shortcut);

            BatchCreateItemVO vo = new BatchCreateItemVO();
            vo.setShortcutId(shortcut.getShortcutId());
            vo.setName(shortcut.getName());
            results.add(vo);
        }
        log.info("批量创建快捷方式成功 userId={} count={}", userId, results.size());
        return results;
    }

    /**
     * 更新快捷方式信息
     * @param userId 用户ID
     * @param shortcutId 快捷方式ID
     * @param req 更新请求
     * @return 更新后的快捷方式
     */
    public ShortcutVO updateShortcut(String userId, String shortcutId, UpdateShortcutRequest req) {

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
     * 删除快捷方式
     * @param userId 用户ID
     * @param shortcutId 快捷方式ID
     */
    public void deleteShortcut(String userId, String shortcutId) {

        NavShortcut shortcut = shortcutMapper.selectById(shortcutId);
        if (shortcut == null || !shortcut.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        shortcutMapper.deleteById(shortcutId);
        log.info("删除快捷方式成功 userId={} shortcutId={}", userId, shortcutId);
    }

    /**
     * 批量更新快捷方式排序
     * @param userId 用户ID
     * @param req 排序请求
     */
    @Transactional
    public void sortShortcuts(String userId, SortRequest req) {

        // 批量查询所有待排序的快捷方�?
        List<String> ids = req.getItems().stream().map(SortItem::getShortcutId).collect(Collectors.toList());
        Map<String, NavShortcut> shortcutMap = shortcutMapper.selectBatchIds(ids).stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toMap(NavShortcut::getShortcutId, Function.identity()));

        for (SortItem item : req.getItems()) {
            NavShortcut shortcut = shortcutMap.get(item.getShortcutId());
            if (shortcut != null) {
                shortcut.setSortOrder(item.getSortOrder());
                shortcutMapper.updateById(shortcut);
            }
        }
    }

    /**
     * 根据URL抓取站点Favicon地址
     * 先查询Redis缓存，若未命中则通过Jsoup请求页面HTML解析 <link rel="icon"> 标签，若失败则回退�?/favicon.ico
     * @param url 站点URL
     * @return Favicon信息
     */
    public FaviconVO fetchFavicon(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                throw new BizException(ResultCode.BAD_REQUEST);
            }

            // 1. 先尝试从 Redis 缓存中获�?
            String cacheKey = RedisConstants.KEY_NAV_FAVICON + host;
            try {
                String cachedUrl = (String) redisTemplate.opsForValue().get(cacheKey);
                if (cachedUrl != null) {
                    log.info("Favicon 缓存命中 host: {} -> {}", host, cachedUrl);
                    FaviconVO vo = new FaviconVO();
                    vo.setFaviconUrl(cachedUrl);
                    vo.setSourceUrl(url);
                    return vo;
                }
            } catch (Exception e) {
                log.warn("�?Redis 读取 Favicon 缓存失败: {}", e.getMessage());
            }

            // 2. 缓存未命中，进行网络爬取与解�?
            String faviconUrl = tryExtractFromHtml(url, scheme, host);
            if (faviconUrl == null) {
                faviconUrl = scheme + "://" + host + "/favicon.ico";
            }

            // 3. 将结果写�?Redis 缓存（缓�?7 天）
            try {
                redisTemplate.opsForValue().set(cacheKey, faviconUrl, 7, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("写入 Redis Favicon 缓存失败: {}", e.getMessage());
            }

            FaviconVO vo = new FaviconVO();
            vo.setFaviconUrl(faviconUrl);
            vo.setSourceUrl(url);
            return vo;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }
    }

    /** 请求页面HTML并使�?Jsoup 解析 <link rel="icon"> 标签，提取图标URL */
    private String tryExtractFromHtml(String pageUrl, String scheme, String host) {
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .get();

            // 优先匹配 apple-touch-icon (通常是高清图�?
            Element iconElement = doc.selectFirst("link[rel~=(?i)^(apple-touch-icon|apple-touch-icon-precomposed)$]");
            if (iconElement == null) {
                // 其次匹配普通的 icon �?shortcut icon
                iconElement = doc.selectFirst("link[rel~=(?i)^(shortcut )?icon$]");
            }

            if (iconElement != null) {
                String href = iconElement.attr("href");
                if (href != null && !href.isEmpty()) {
                    return resolveFaviconUrl(href, scheme, host);
                }
            }
        } catch (Exception e) {
            log.warn("从页�?{} �?Jsoup 解析 Favicon 失败: {}", pageUrl, e.getMessage());
        }
        return null;
    }

    /** 解析可能为相对路径的图标URL */
    private static String resolveFaviconUrl(String href, String scheme, String host) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        }
        if (href.startsWith("//")) {
            return scheme + ":" + href;
        }
        String base = scheme + "://" + host;
        return href.startsWith("/") ? base + href : base + "/" + href;
    }

    /** 支持的图片扩展名映射（MIME �?扩展名） */
    private static final Map<String, String> MIME_TO_EXT = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/gif", "gif",
            "image/webp", "webp",
            "image/x-icon", "ico",
            "image/vnd.microsoft.icon", "ico",
            "image/svg+xml", "svg");

    /**
     * 上传图标文件
     * 安全限制：文件类型白名单、大小上�?200KB、每用户每小时最�?30 �?
     * @param userId 用户ID
     * @param file 图标文件
     * @return 图标可访问URL
     */
    public IconUploadVO uploadIcon(String userId, MultipartFile file) {
        // 1. 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            log.warn("图标上传类型不合�?userId={}, contentType={}", userId, contentType);
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "不支持的图片格式，仅允许 PNG/JPEG/GIF/WebP/ICO/SVG");
        }

        // 2. 文件大小校验
        if (file.getSize() > MAX_ICON_SIZE) {
            log.warn("图标上传超出大小限制 userId={}, size={}", userId, file.getSize());
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "图标文件不能超过 200KB");
        }

        // 3. 上传频率限制（Redis 计数器）
        String rateKey = RedisConstants.KEY_NAV_RATE_UPLOAD + userId;
        Long count = redisTemplate.opsForValue().increment(rateKey);
        if (count != null && count == 1) {
            redisTemplate.expire(rateKey, 1, TimeUnit.HOURS);
        }
        if (count != null && count > MAX_UPLOADS_PER_HOUR) {
            log.warn("图标上传频率超限 userId={}, count={}", userId, count);
            throw new BizException(ResultCode.TOO_MANY_REQUESTS);
        }

        // 4. 保存文件
        try {
            String targetDir = iconPath + java.io.File.separator + userId;
            String uniqueFileName = com.navatation.common.FileUploadUtil.saveFile(file, targetDir);
            log.info("图标上传成功 userId={}, filename={}", userId, uniqueFileName);

            return new IconUploadVO("/uploads/icon/custom/" + userId + "/" + uniqueFileName);
        } catch (Exception e) {
            log.error("图标文件保存失败 userId={}", userId, e);
            throw new BizException(ResultCode.INTERNAL_ERROR);
        }
    }

    /**
     * 获取推荐站点列表
     * @return 推荐分类及站点列�?
     */
    public List<RecommendCategoryVO> getRecommended() {
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
            RecommendCategoryVO vo = new RecommendCategoryVO();
            vo.setCategoryId(cat.getCategoryId());
            vo.setCategoryName(cat.getName());
            vo.setCategoryIcon(cat.getIcon());
            vo.setSortOrder(cat.getSortOrder());
            List<RecommendSiteVO> siteVOs = siteMap.getOrDefault(cat.getCategoryId(), new ArrayList<>()).stream().map(site -> {
                RecommendSiteVO siteVO = new RecommendSiteVO();
                siteVO.setSiteId(site.getSiteId());
                siteVO.setCategoryId(site.getCategoryId());
                siteVO.setName(site.getName());
                siteVO.setUrl(site.getUrl());
                siteVO.setIconType(site.getIconType());
                siteVO.setIconValue(site.getIconValue());
                siteVO.setIconColor(site.getIconColor());
                siteVO.setSortOrder(site.getSortOrder());
                return siteVO;
            }).collect(Collectors.toList());
            vo.setSites(siteVOs);
            return vo;
        }).collect(Collectors.toList());
    }

    @Transactional
    public RecommendCategoryVO addRecommendCategory(String userId, RecommendCategoryRequest req) {
        if (!isAdmin(userId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        RecommendCategory cat = new RecommendCategory();
        cat.setCategoryId(IdUtils.genRecommendCategoryId());
        cat.setName(req.getName());
        cat.setIcon(req.getIcon());
        cat.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
        recommendCategoryMapper.insert(cat);
        
        RecommendCategoryVO vo = new RecommendCategoryVO();
        vo.setCategoryId(cat.getCategoryId());
        vo.setCategoryName(cat.getName());
        vo.setCategoryIcon(cat.getIcon());
        vo.setSortOrder(cat.getSortOrder());
        vo.setSites(new ArrayList<>());
        return vo;
    }
    
    @Transactional
    public void updateRecommendCategory(String userId, String categoryId, RecommendCategoryRequest req) {
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
    
    @Transactional
    public RecommendSiteVO addRecommendSite(String userId, RecommendSiteRequest req) {
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
        site.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
        recommendSiteMapper.insert(site);
        
        RecommendSiteVO vo = new RecommendSiteVO();
        vo.setSiteId(site.getSiteId());
        vo.setCategoryId(site.getCategoryId());
        vo.setName(site.getName());
        vo.setUrl(site.getUrl());
        vo.setIconType(site.getIconType());
        vo.setIconValue(site.getIconValue());
        vo.setIconColor(site.getIconColor());
        vo.setSortOrder(site.getSortOrder());
        return vo;
    }
    
    @Transactional
    public void updateRecommendSite(String userId, String siteId, RecommendSiteRequest req) {
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
        if (req.getSortOrder() != null) site.setSortOrder(req.getSortOrder());
        recommendSiteMapper.updateById(site);
    }
    
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



    /** 实体转VO */
    private ShortcutVO toShortcutVO(NavShortcut s) {
        ShortcutVO vo = new ShortcutVO();
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
