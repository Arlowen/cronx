package io.cronx.web.service.asyncjob;

import java.util.List;

import org.springframework.stereotype.Service;

import io.cronx.async.task.framework.manager.AsyncJobManager;
import io.cronx.async.task.framework.model.AsyncJob;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.fo.asynctask.AddTaskWithJobFO;
import io.cronx.web.model.vo.asyncjob.QueryJobVO;

@Service
public interface AsyncJobService {

    void createJob(String jobDesc);

    void deleteByJobId(long jobId);

    QueryJobVO listJobPageByCondition(long pageSize, long pageNumber);

    AsyncJobManager getAsyncJobManager();

    void startAsyncJob(Long jobId);

    void stopAsyncJob(Long asyncJobId);

    AsyncJob genAsyncJobFromDO(AsyncJobDO asyncJobDO);
}
