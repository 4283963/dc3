package com.dc3.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc3.domain.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
