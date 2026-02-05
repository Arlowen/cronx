package io.cronx.web.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.web.model.entity.AsyncTaskDO;
import io.cronx.web.model.entity.KvBaseConfigDO;
import org.apache.ibatis.annotations.Param;

public interface KvBaseConfigMapper extends BaseMapper<KvBaseConfigDO> {

    KvBaseConfigDO getConfigByTaskIdAndConfigName(long taskId, String configName);

    List<KvBaseConfigDO> queryListByTaskId(long taskId);

    void updateTaskConfig(long taskId, String configName, String configValue);

    void deleteTaskConfig(long taskId);

    void deleteByTaskIds(Collection<Long> taskIds);

    void batchInsertConfig(@Param("configs") List<KvBaseConfigDO> configs);
}
