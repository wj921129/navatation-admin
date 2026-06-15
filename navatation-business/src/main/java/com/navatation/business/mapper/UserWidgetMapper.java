package com.navatation.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.navatation.business.entity.nav.UserWidget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件数据库接口映射器
 */
@Mapper
public interface UserWidgetMapper extends BaseMapper<UserWidget> {
    
    @Delete("DELETE FROM navatation_user_widget WHERE user_id = #{userId}")
    void physicalDeleteByUserId(@Param("userId") String userId);
}
