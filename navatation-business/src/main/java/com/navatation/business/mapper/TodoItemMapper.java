package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.TodoItem;
import org.apache.ibatis.annotations.Mapper;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 待办事项数据访问层 */
@Mapper
public interface TodoItemMapper extends BaseMapper<TodoItem> {
}
