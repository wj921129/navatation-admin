package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navatation.business.dto.req.recommend.RecommendWidgetReqDTO;
import com.navatation.business.dto.resp.recommend.RecommendWidgetRespDTO;
import com.navatation.business.entity.recommend.RecommendWidget;
import com.navatation.business.mapper.RecommendWidgetMapper;
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
 * 推荐小组件服务，提供管理员全局小组件的增删改查
 */
@Service
@RequiredArgsConstructor
public class RecommendWidgetService {

    private static final Logger log = LoggerFactory.getLogger(RecommendWidgetService.class);

    private final RecommendWidgetMapper recommendWidgetMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取所有推荐的小组件
     */
    public List<RecommendWidgetRespDTO> getRecommendWidgets() {
        List<RecommendWidget> list = recommendWidgetMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 批量覆盖保存推荐小组件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveRecommendWidgets(List<RecommendWidgetReqDTO> requests) {
        recommendWidgetMapper.delete(new LambdaQueryWrapper<>());
        
        if (CollectionUtils.isEmpty(requests)) {
            log.info("清空所有推荐小组件");
            return;
        }

        for (RecommendWidgetReqDTO req : requests) {
            RecommendWidget entity = new RecommendWidget();
            entity.setWidgetId(IdUtils.genWidgetId());
            entity.setType(req.getType());
            entity.setStyle(req.getStyle());
            entity.setX(req.getX());
            entity.setY(req.getY());
            
            if (req.getMeta() != null) {
                try {
                    entity.setMeta(objectMapper.writeValueAsString(req.getMeta()));
                } catch (JsonProcessingException e) {
                    log.error("推荐小组件元数据序列化失败", e);
                    entity.setMeta("{}");
                }
            }
            
            recommendWidgetMapper.insert(entity);
        }
        log.info("保存推荐小组件成功，数量: {}", requests.size());
    }

    private RecommendWidgetRespDTO toVO(RecommendWidget entity) {
        RecommendWidgetRespDTO vo = new RecommendWidgetRespDTO();
        vo.setWidgetId(entity.getWidgetId());
        vo.setType(entity.getType());
        vo.setStyle(entity.getStyle());
        vo.setX(entity.getX());
        vo.setY(entity.getY());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        
        String dataStr = entity.getMeta();
        if (StringUtils.isBlank(dataStr)) {
            vo.setMeta(Collections.emptyMap());
            return vo;
        }
        
        try {
            Map<String, Object> dataMap = objectMapper.readValue(dataStr, new TypeReference<Map<String, Object>>() {});
            vo.setMeta(dataMap);
        } catch (JsonProcessingException e) {
            log.error("推荐小组件元数据反序列化失败 widgetId={}", entity.getWidgetId(), e);
            vo.setMeta(Collections.emptyMap());
        }
        return vo;
    }
}
