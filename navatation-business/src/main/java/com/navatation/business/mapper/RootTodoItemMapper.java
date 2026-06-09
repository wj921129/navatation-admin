package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.root.RootTodoItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员端待办事项 Mapper 接口
 *
 * @date 2026-06-09
 */
@Mapper
public interface RootTodoItemMapper extends BaseMapper<RootTodoItem> {
}
