package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.root.RootUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author admin
 * @CreateTime 2026-06-09
 * @Description 管理员用户 Mapper 接口
 */
@Mapper
public interface RootUserMapper extends BaseMapper<RootUser> {
}
