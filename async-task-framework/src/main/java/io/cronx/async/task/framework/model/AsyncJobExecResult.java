package io.cronx.async.task.framework.model;

import java.util.Date;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import lombok.Data;

@Data
public class AsyncJobExecResult {

    private Long           jobId;

    private AsyncJobStatus asyncJobStatus;

    private AsyncTaskBase  currentTask;

    private String         curTaskFailedResult;

    private Date           startTime;

    private Date           finishTime;

    public AsyncJobExecResult(Long jobId, AsyncJobStatus asyncJobStatus, AsyncTaskBase currentTask, String curTaskFailedResult, Date startTime, Date finishTime){
        this.jobId = jobId;
        this.asyncJobStatus = asyncJobStatus;
        this.currentTask = currentTask;
        this.curTaskFailedResult = curTaskFailedResult;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
