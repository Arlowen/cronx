package io.cronx.web.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.web.model.entity.AsyncTaskDO;

public interface AsyncTaskMapper extends BaseMapper<AsyncTaskDO> {

    AsyncTaskDO queryByTaskId(long taskId);

    List<AsyncTaskDO> queryByTaskIds(@Param("ids") List<Long> taskIds);

    List<AsyncTaskDO> queryByCondition(@Param("offset") Long offset, @Param("pageSize") Long pageSize, @Param("jobId") Long jobId);

    Integer queryCountByCondition(long jobId);

    void deleteByTaskId(long taskId);

    List<AsyncTaskDO> queryListByJobId(long jobId);

    void deleteByJobId(long jobId);

    AsyncTaskDO queryTaskByName(String taskName);

    void resetTaskStatusToWaitStartBatch(@Param("ids") List<Long> jobIds);

    void batchInsertTask(@Param("tasks") List<AsyncTaskDO> tasks);
}
