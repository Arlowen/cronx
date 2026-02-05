package io.cronx.web.component.async;

import java.util.Date;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.model.AsyncJobExecResult;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.web.mapper.AsyncJobMapper;
import io.cronx.web.mapper.AsyncTaskMapper;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.entity.AsyncTaskDO;

public class JobPersistCallback implements Consumer<AsyncJobExecResult> {

    private final AsyncJobMapper  asyncJobMapper;

    private final AsyncTaskMapper asyncTaskMapper;

    public JobPersistCallback(AsyncJobMapper asyncJobMapper, AsyncTaskMapper asyncTaskMapper){
        this.asyncJobMapper = asyncJobMapper;
        this.asyncTaskMapper = asyncTaskMapper;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void accept(AsyncJobExecResult asyncJobExecResult) {
        AsyncJobDO asyncJobDO = asyncJobMapper.selectById(asyncJobExecResult.getJobId());
        // update field
        asyncJobDO.setStartTime(asyncJobExecResult.getStartTime());
        asyncJobDO.setJobStatus(asyncJobExecResult.getAsyncJobStatus());
        if (asyncJobExecResult.getAsyncJobStatus() == AsyncJobStatus.FINISHED) {
            asyncJobDO.setFinishTime(asyncJobExecResult.getFinishTime());
            long nextTimeMs = System.currentTimeMillis() + 5 * 1000;
            asyncJobDO.setNextStartTime(new Date(nextTimeMs));
        } else {
            asyncJobDO.setNextStartTime(null);
        }
        asyncJobMapper.updateById(asyncJobDO);

        AsyncTaskBase curTask = asyncJobExecResult.getCurrentTask();
        Long taskId = curTask.getTaskContext().getTaskId();

        AsyncTaskDO curTaskDO = asyncTaskMapper.queryByTaskId(taskId);
        curTaskDO.setErrorMsg(curTask.getErrMsg());
        curTaskDO.setTaskStatus(curTask.getTaskStatus());
        asyncTaskMapper.updateById(curTaskDO);
    }
}
