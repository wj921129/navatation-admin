package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.NavCategory;
import org.apache.ibatis.annotations.Mapper;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航分类数据访问层 */
@Mapper
public interface NavCategoryMapper extends BaseMapper<NavCategory> {
}
