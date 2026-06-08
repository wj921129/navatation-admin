package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户数据访问层 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
