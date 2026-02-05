package io.cronx.web.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.web.model.entity.AsyncJobDO;

public interface AsyncJobMapper extends BaseMapper<AsyncJobDO> {

    AsyncJobDO queryJobByName(String jobName);

    AsyncJobDO queryJobById(long jobId);

    void deleteByJobId(long jobId);

    List<AsyncJobDO> queryByCondition(@Param("offset") long offset, @Param("pageSize") long pageSize);

    Integer queryCountByCondition();

    List<AsyncJobDO> queryAsyncJobsByStatus(@Param("jobStatus") AsyncJobStatus asyncJobStatus);

    void updateStatus(@Param("status") String jobStatus, @Param("asyncJobId") Long asyncJobId);

    void updateStatusBatch(@Param("jobStatus") AsyncJobStatus jobStatus, @Param("ids") List<Long> ids);

    List<AsyncJobDO> queryNeedStartTimerAsyncJobs(Date currentTs);

    void startTimerFinishedJobs(Date currentTs);
}
