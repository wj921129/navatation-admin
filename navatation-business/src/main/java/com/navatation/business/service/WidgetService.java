package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navatation.business.dto.WidgetRequest;
import com.navatation.business.dto.WidgetVO;
import com.navatation.business.entity.UserWidget;
import com.navatation.business.mapper.UserWidgetMapper;
import com.navatation.common.IdUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件服务，处理用户组件的查询与批量覆盖保存
 */
@Service
@RequiredArgsConstructor
public class WidgetService {

    private static final Logger log = LoggerFactory.getLogger(WidgetService.class);

    private final UserWidgetMapper widgetMapper;
    private final ObjectMapper objectMapper;
    private final com.navatation.business.mapper.UserMapper userMapper;
    private final RecommendWidgetService recommendWidgetService;

    private boolean isAdmin(String userId) {
        com.navatation.business.entity.User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * 获取用户配置的所有组件列表
     *
     * @param userId 用户唯一ID
     * @return 组件展现VO列表
     */
    public List<WidgetVO> getWidgets(String userId) {
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
     *
     * @param userId 用户唯一ID
     * @param requests 待保存的组件请求列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWidgets(String userId, List<WidgetRequest> requests) {
        widgetMapper.delete(new LambdaQueryWrapper<UserWidget>().eq(UserWidget::getUserId, userId));

        if (CollectionUtils.isEmpty(requests)) {
            log.info("保存用户组件 传入列表为空，清除用户所有组件 userId={}", userId);
            return;
        }

        for (WidgetRequest req : requests) {
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
            widgetMapper.insert(entity);
        }
        log.info("保存用户组件成功 userId={}, count={}", userId, requests.size());
    }

    /**
     * 将 UserWidget 实体对象转换为 WidgetVO 对象
     */
    private WidgetVO toVO(UserWidget entity) {
        WidgetVO vo = new WidgetVO();
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
