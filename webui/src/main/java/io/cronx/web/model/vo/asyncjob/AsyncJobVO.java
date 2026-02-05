package io.cronx.web.model.vo.asyncjob;

import java.util.Date;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.enumeration.LifeCycleState;
import lombok.Data;

@Data
public class AsyncJobVO {

    private long           id;

    private String         jobName;

    private String         jobDesc;

    private AsyncJobStatus jobStatus;

    private LifeCycleState lifeCycleState;

    private Date           startTime;

    private Date           finishTime;

    private Date           nextStartTime;

    private Boolean        isTiming;

    public void convertVO(AsyncJobDO jobDO) {
        this.id = jobDO.getId();
        this.jobName = jobDO.getJobName();
        this.jobDesc = jobDO.getJobDesc();
        this.jobStatus = jobDO.getJobStatus();
        this.lifeCycleState = jobDO.getLifeCycleState();
        this.startTime = jobDO.getStartTime();
        this.finishTime = jobDO.getFinishTime();
        this.nextStartTime = jobDO.getNextStartTime();
        this.isTiming = jobDO.getIsTiming();
    }
}
