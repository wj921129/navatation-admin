package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.recommend.RecommendWidget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author wanggy
 * @CreateTime 2026-06-08
 * @Description 推荐小组件 Mapper
 */
@Mapper
public interface RecommendWidgetMapper extends BaseMapper<RecommendWidget> {

    @Delete("DELETE FROM navatation_recommend_widget")
    void physicalDeleteAll();
}
