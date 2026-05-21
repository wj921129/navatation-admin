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
import com.navatation.common.BizException;
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航服务，处理分类和快捷方式的CRUD、排序、Favicon抓取及推荐站点管理 */
@Service
@RequiredArgsConstructor
public class NavService {

    private static final Logger log = LoggerFactory.getLogger(NavService.class);

    private static final String ICON_TYPE_BUILTIN = "BUILTIN";
    private static final String DEFAULT_CATEGORY_NAME = "常用";

    /** 允许上传的图标 MIME 类型白名单 */
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp",
            "image/x-icon", "image/vnd.microsoft.icon", "image/svg+xml");

    /** 图标文件大小上限：200KB */
    private static final long MAX_ICON_SIZE = 200 * 1024;

    /** 每小时每用户最大上传次数 */
    private static final int MAX_UPLOADS_PER_HOUR = 30;

    private final NavCategoryMapper categoryMapper;
    private final NavShortcutMapper shortcutMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${app.upload.icon-path}")
    private String iconPath;
    /**
     * 查询用户所有分类及各自快捷方式数量
     * @param userId 用户ID
     * @return 分类列表 */
    public List<CategoryVO> getCategories(Long userId) {
        List<NavCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<NavCategory>()
                        .eq(NavCategory::getUserId, userId)
                        .orderByAsc(NavCategory::getSortOrder));

        // 批量查询各分类下的快捷方式数量
        List<Long> categoryIds = categories.stream().map(NavCategory::getCategoryId).collect(Collectors.toList());
        Map<Long, Long> countMap = categoryIds.isEmpty() ? Map.of() :
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
     * @return 创建的分类 */
    public CategoryVO createCategory(Long userId, CategoryRequest req) {
        NavCategory category = new NavCategory();
        category.setUserId(userId);
        category.setName(req.getName());
        category.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
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
     * 更新分类名称或排序
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param req 分类请求 */
    public void updateCategory(Long userId, Long categoryId, CategoryRequest req) {
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
     * 删除分类及其下所有快捷方式
     * @param userId 用户ID
     * @param categoryId 分类ID */
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        NavCategory category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        // 删除该分类下的所有快捷方式
        shortcutMapper.delete(new LambdaQueryWrapper<NavShortcut>()
                .eq(NavShortcut::getCategoryId, categoryId));
        categoryMapper.deleteById(categoryId);
        log.info("删除分类成功 userId={} categoryId={}", userId, categoryId);
    }

    /**
     * 查询用户快捷方式列表，可按分类筛选
     * @param userId 用户ID
     * @param categoryId 分类ID（可为null表示全部）
     * @return 快捷方式列表 */
    public List<ShortcutVO> getShortcuts(Long userId, Long categoryId) {
        LambdaQueryWrapper<NavShortcut> wrapper = new LambdaQueryWrapper<NavShortcut>()
                .eq(NavShortcut::getUserId, userId)
                .orderByAsc(NavShortcut::getSortOrder);
        if (categoryId != null) {
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
     * @return 创建结果列表 */
    @Transactional
    public List<BatchCreateItemVO> batchCreate(Long userId, BatchCreateRequest req) {
        // 确保默认分类存在
        Long categoryId = req.getCategoryId();
        if (categoryId == null) {
            NavCategory defaultCat = categoryMapper.selectOne(
                    new LambdaQueryWrapper<NavCategory>()
                            .eq(NavCategory::getUserId, userId)
                            .eq(NavCategory::getName, DEFAULT_CATEGORY_NAME));
            if (defaultCat == null) {
                defaultCat = new NavCategory();
                defaultCat.setUserId(userId);
                defaultCat.setName(DEFAULT_CATEGORY_NAME);
                defaultCat.setSortOrder(0);
                categoryMapper.insert(defaultCat);
            }
            categoryId = defaultCat.getCategoryId();
        } else {
            // 校验 categoryId 是否属于当前用户，防止水平越权
            NavCategory cat = categoryMapper.selectById(categoryId);
            if (cat == null || !cat.getUserId().equals(userId)) {
                throw new BizException(ResultCode.NOT_FOUND);
            }
        }

        int maxSort = shortcutMapper.selectList(
                new LambdaQueryWrapper<NavShortcut>()
                        .eq(NavShortcut::getCategoryId, categoryId))
                .stream().mapToInt(NavShortcut::getSortOrder).max().orElse(0);

        List<BatchCreateItemVO> results = new ArrayList<>();
        for (CreateShortcutItem item : req.getShortcuts()) {
            NavShortcut shortcut = new NavShortcut();
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
     * @return 更新后的快捷方式 */
    public ShortcutVO updateShortcut(Long userId, Long shortcutId, UpdateShortcutRequest req) {
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
     * @param shortcutId 快捷方式ID */
    public void deleteShortcut(Long userId, Long shortcutId) {
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
     * @param req 排序请求 */
    @Transactional
    public void sortShortcuts(Long userId, SortRequest req) {
        // 批量查询所有待排序的快捷方式
        List<Long> ids = req.getItems().stream().map(SortItem::getShortcutId).collect(Collectors.toList());
        Map<Long, NavShortcut> shortcutMap = shortcutMapper.selectBatchIds(ids).stream()
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

    private static final Pattern FAVICON_PATTERN = Pattern.compile(
            "<link[^>]*rel=[\"'](?:shortcut\\s+)?icon[\"'][^>]*href=[\"']([^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE);

    /**
     * 根据URL抓取站点Favicon地址
     * 先请求页面HTML解析 <link rel="icon"> 标签，若失败则回退到 /favicon.ico
     * @param url 站点URL
     * @return Favicon信息 */
    public FaviconVO fetchFavicon(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                throw new BizException(ResultCode.BAD_REQUEST);
            }

            String faviconUrl = tryExtractFromHtml(url, scheme, host);
            if (faviconUrl == null) {
                faviconUrl = scheme + "://" + host + "/favicon.ico";
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

    /** 请求页面HTML并解析 <link rel=\"icon\"> 标签，提取图标URL */
    private String tryExtractFromHtml(String pageUrl, String scheme, String host) {
        try {
            org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
            rt.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                setConnectTimeout(5000);
                setReadTimeout(5000);
            }});
            String html = rt.getForObject(pageUrl, String.class);
            if (html == null || html.isEmpty()) return null;

            Matcher matcher = FAVICON_PATTERN.matcher(html);
            if (matcher.find()) {
                return resolveFaviconUrl(matcher.group(1), scheme, host);
            }
        } catch (Exception e) {
            log.warn("从页面 {} 解析Favicon失败: {}", pageUrl, e.getMessage());
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

    /** 支持的图片扩展名映射（MIME → 扩展名） */
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
     * 安全限制：文件类型白名单、大小上限 200KB、每用户每小时最多 30 次
     * @param userId 用户ID
     * @param file 图标文件
     * @return 图标可访问URL */
    public IconUploadVO uploadIcon(Long userId, MultipartFile file) {
        // 1. 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            log.warn("图标上传类型不合法 userId={}, contentType={}", userId, contentType);
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "不支持的图片格式，仅允许 PNG/JPEG/GIF/WebP/ICO/SVG");
        }

        // 2. 文件大小校验
        if (file.getSize() > MAX_ICON_SIZE) {
            log.warn("图标上传超出大小限制 userId={}, size={}", userId, file.getSize());
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "图标文件不能超过 200KB");
        }

        // 3. 上传频率限制（Redis 计数器）
        String rateKey = "rate:icon_upload:" + userId;
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
            String targetDir = iconPath + java.io.File.separator + "U" + userId;
            String uniqueFileName = com.navatation.common.FileUploadUtil.saveFile(file, targetDir);
            log.info("图标上传成功 userId={}, filename={}", userId, uniqueFileName);

            return new IconUploadVO("/uploads/icon/custom/U" + userId + "/" + uniqueFileName);
        } catch (Exception e) {
            log.error("图标文件保存失败 userId={}", userId, e);
            throw new BizException(ResultCode.INTERNAL_ERROR);
        }
    }

    /**
     * 获取推荐站点列表（硬编码数据）
     * @return 推荐分类及站点列表 */
    public List<RecommendCategoryVO> getRecommended() {
        // 返回硬编码的推荐数据（与前端 AddShortcutDialog 保持一致）
        List<RecommendCategoryVO> categories = new ArrayList<>();

        categories.add(buildCategory(1L, "看视频", "Video",
                site("YouTube", "https://youtube.com", "#FF0000"),
                site("Netflix", "https://netflix.com", "#E50914"),
                site("Bilibili", "https://bilibili.com", "#00A1D6"),
                site("Twitch", "https://twitch.tv", "#9146FF")));

        categories.add(buildCategory(2L, "AI工具", "Cpu",
                site("ChatGPT", "https://chat.openai.com", "#10A37F"),
                site("Claude", "https://claude.ai", "#CC9B7A"),
                site("Midjourney", "https://midjourney.com", "#000000"),
                site("Gemini", "https://gemini.google.com", "#4285F4")));

        categories.add(buildCategory(3L, "Web开发", "Code",
                site("GitHub", "https://github.com", "#181717"),
                site("Stack Overflow", "https://stackoverflow.com", "#F58025"),
                site("CodePen", "https://codepen.io", "#000000"),
                site("MDN", "https://developer.mozilla.org", "#000000")));

        categories.add(buildCategory(4L, "购物", "ShoppingBag",
                site("Amazon", "https://amazon.com", "#FF9900"),
                site("淘宝", "https://taobao.com", "#FF6A00"),
                site("京东", "https://jd.com", "#E3393C"),
                site("eBay", "https://ebay.com", "#E53238")));

        categories.add(buildCategory(5L, "新闻资讯", "Newspaper",
                site("Reddit", "https://reddit.com", "#FF4500"),
                site("Hacker News", "https://news.ycombinator.com", "#FF6600"),
                site("Medium", "https://medium.com", "#000000"),
                site("BBC", "https://bbc.com", "#000000")));

        categories.add(buildCategory(6L, "游戏", "Gamepad2",
                site("Steam", "https://store.steampowered.com", "#171A21"),
                site("Epic Games", "https://epicgames.com", "#313131"),
                site("IGN", "https://ign.com", "#D8281F"),
                site("GameSpot", "https://gamespot.com", "#FF0000")));

        categories.add(buildCategory(7L, "音乐", "Music",
                site("Spotify", "https://spotify.com", "#1DB954"),
                site("Apple Music", "https://music.apple.com", "#FA243C"),
                site("SoundCloud", "https://soundcloud.com", "#FF5500"),
                site("YouTube Music", "https://music.youtube.com", "#FF0000")));

        categories.add(buildCategory(8L, "办公效率", "Briefcase",
                site("Notion", "https://notion.so", "#000000"),
                site("Slack", "https://slack.com", "#4A154B"),
                site("Trello", "https://trello.com", "#0052CC"),
                site("Figma", "https://figma.com", "#F24E1E")));

        return categories;
    }

    /** 构建推荐分类 */
    private RecommendCategoryVO buildCategory(Long id, String name, String icon, RecommendSiteVO... sites) {
        RecommendCategoryVO vo = new RecommendCategoryVO();
        vo.setCategoryId(id);
        vo.setCategoryName(name);
        vo.setCategoryIcon(icon);
        vo.setSites(List.of(sites));
        return vo;
    }

    /** 构建推荐站点 */
    private RecommendSiteVO site(String name, String url, String color) {
        RecommendSiteVO vo = new RecommendSiteVO();
        vo.setName(name);
        vo.setUrl(url);
        vo.setIconType("BUILTIN");
        vo.setIconValue(name);
        vo.setIconColor(color);
        return vo;
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
