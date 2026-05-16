package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.UserConfig;
import org.apache.ibatis.annotations.Mapper;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户配置数据访问层 */
@Mapper
public interface UserConfigMapper extends BaseMapper<UserConfig> {
}
