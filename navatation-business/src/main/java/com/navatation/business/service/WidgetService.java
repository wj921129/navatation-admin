package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navatation.business.dto.req.widget.WidgetReqDTO;
import com.navatation.business.dto.resp.widget.WidgetRespDTO;
import com.navatation.business.entity.nav.UserWidget;
import com.navatation.business.entity.recommend.RecommendWidget;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.RecommendWidgetMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.mapper.UserWidgetMapper;
import com.navatation.common.IdUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户组件服务，处理用户组件的查询与批量覆盖保存
 */
@Service
@RequiredArgsConstructor
public class WidgetService {

    private static final Logger log = LoggerFactory.getLogger(WidgetService.class);

    private final UserWidgetMapper widgetMapper;
    private final RecommendWidgetMapper recommendWidgetMapper;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * 获取用户配置的所有组件列表
     */
    public List<WidgetRespDTO> getWidgets(String userId) {
        if (isAdmin(userId)) {
            List<RecommendWidget> list = recommendWidgetMapper.selectList(new LambdaQueryWrapper<>());
            List<WidgetRespDTO> voList;
            if (CollectionUtils.isEmpty(list)) {
                voList = Collections.emptyList();
            } else {
                voList = list.stream().map(this::toVO).collect(Collectors.toList());
            }
            try {
                String cacheKey = "navatation:guest_config";
                String cachedStr = (String) redisTemplate.opsForHash().get(cacheKey, "widgets");
                String currentStr = objectMapper.writeValueAsString(voList);
                if (cachedStr == null || !cachedStr.equals(currentStr)) {
                    redisTemplate.opsForHash().put(cacheKey, "widgets", currentStr);
                    log.info("管理员获取组件时发现缓存不一致，已刷新游客组件缓存");
                }
            } catch (Exception e) {
                log.warn("刷新游客组件缓存失败", e);
            }
            return voList;
        }

        List<UserWidget> list = widgetMapper.selectList(
                new LambdaQueryWrapper<UserWidget>()
                        .eq(UserWidget::getUserId, userId)
        );
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 批量覆盖保存用户的组件配置（删除原有配置并插入新配置）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWidgets(String userId, List<WidgetReqDTO> requests) {
        if (isAdmin(userId)) {
            recommendWidgetMapper.delete(new LambdaQueryWrapper<>());

            if (CollectionUtils.isEmpty(requests)) {
                log.info("保存管理员推荐组件 传入列表为空，清除管理员所有组件 userId={}", userId);
                redisTemplate.opsForHash().delete("navatation:guest_config", "widgets");
                return;
            }

            List<RecommendWidget> saveList = new ArrayList<>();
            for (WidgetReqDTO req : requests) {
                RecommendWidget entity = new RecommendWidget();
                entity.setType(req.getType());
                entity.setStyle(req.getStyle());
                entity.setX(req.getX());
                entity.setY(req.getY());

                String widgetId = req.getWidgetId();
                if (StringUtils.isBlank(widgetId) || !widgetId.startsWith("WG")) {
                    entity.setWidgetId(IdUtils.genWidgetId());
                } else {
                    entity.setWidgetId(widgetId);
                }

                if (req.getMeta() != null) {
                    try {
                        entity.setMeta(objectMapper.writeValueAsString(req.getMeta()));
                    } catch (JsonProcessingException e) {
                        log.error("组件元数据序列化失败 userId={}", userId, e);
                        entity.setMeta("{}");
                    }
                }
                saveList.add(entity);
            }
            if (!saveList.isEmpty()) {
                Db.saveBatch(saveList);
            }
            log.info("保存管理员推荐组件成功 userId={}, count={}", userId, requests.size());
            redisTemplate.opsForHash().delete("navatation:guest_config", "widgets");
            return;
        }

        widgetMapper.delete(new LambdaQueryWrapper<UserWidget>().eq(UserWidget::getUserId, userId));

        if (CollectionUtils.isEmpty(requests)) {
            log.info("保存用户组件 传入列表为空，清除用户所有组件 userId={}", userId);
            return;
        }

        List<UserWidget> saveList = new ArrayList<>();
        for (WidgetReqDTO req : requests) {
            UserWidget entity = new UserWidget();
            entity.setUserId(userId);
            entity.setType(req.getType());
            entity.setStyle(req.getStyle());
            entity.setX(req.getX());
            entity.setY(req.getY());

            String widgetId = req.getWidgetId();
            if (StringUtils.isBlank(widgetId) || !widgetId.startsWith("WG")) {
                entity.setWidgetId(IdUtils.genWidgetId());
            } else {
                entity.setWidgetId(widgetId);
            }

            if (req.getMeta() != null) {
                try {
                    entity.setMeta(objectMapper.writeValueAsString(req.getMeta()));
                } catch (JsonProcessingException e) {
                    log.error("组件元数据序列化失败 userId={}", userId, e);
                    entity.setMeta("{}");
                }
            }
            saveList.add(entity);
        }
        if (!saveList.isEmpty()) {
            Db.saveBatch(saveList);
        }
        log.info("保存用户组件成功 userId={}, count={}", userId, requests.size());
    }

    private WidgetRespDTO toVO(UserWidget entity) {
        WidgetRespDTO vo = new WidgetRespDTO();
        vo.setWidgetId(entity.getWidgetId());
        vo.setType(entity.getType());
        vo.setStyle(entity.getStyle());
        vo.setX(entity.getX());
        vo.setY(entity.getY());

        String metaStr = entity.getMeta();
        if (StringUtils.isBlank(metaStr)) {
            vo.setMeta(Collections.emptyMap());
            return vo;
        }

        try {
            Map<String, Object> metaMap = objectMapper.readValue(metaStr, new TypeReference<Map<String, Object>>() {});
            vo.setMeta(metaMap);
        } catch (JsonProcessingException e) {
            log.error("组件元数据反序列化失败 widgetId={}", entity.getWidgetId(), e);
            vo.setMeta(Collections.emptyMap());
        }
        return vo;
    }

    private WidgetRespDTO toVO(RecommendWidget entity) {
        WidgetRespDTO vo = new WidgetRespDTO();
        vo.setWidgetId(entity.getWidgetId());
        vo.setType(entity.getType());
        vo.setStyle(entity.getStyle());
        vo.setX(entity.getX());
        vo.setY(entity.getY());

        String metaStr = entity.getMeta();
        if (StringUtils.isBlank(metaStr)) {
            vo.setMeta(Collections.emptyMap());
            return vo;
        }

        try {
            Map<String, Object> metaMap = objectMapper.readValue(metaStr, new TypeReference<Map<String, Object>>() {});
            vo.setMeta(metaMap);
        } catch (JsonProcessingException e) {
            log.error("组件元数据反序列化失败 widgetId={}", entity.getWidgetId(), e);
            vo.setMeta(Collections.emptyMap());
        }
        return vo;
    }
}
