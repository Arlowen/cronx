package io.cronx.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.web.model.entity.ApiSourceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiSourceMapper extends BaseMapper<ApiSourceDO> {

    List<ApiSourceDO> queryByCondition(@Param("offset") long offset, @Param("pageSize") long pageSize);

    Integer queryCountByCondition();
}
