package io.cronx.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.web.model.entity.ApiKvBaseConfigDO;

public interface ApiKvBaseConfigMapper extends BaseMapper<ApiKvBaseConfigDO> {

    ApiKvBaseConfigDO getConfigByApiIdAndConfigName(long apiSourceId, String configName);

    List<ApiKvBaseConfigDO> queryListByApiSourceId(long apiSourceId);

    void updateApiSourceConfig(long apiSourceId, String configName, String configValue);

    void deleteApiSourceConfig(long apiSourceId);

    void batchInsertConfig(@Param("configs") List<ApiKvBaseConfigDO> configs);
}
