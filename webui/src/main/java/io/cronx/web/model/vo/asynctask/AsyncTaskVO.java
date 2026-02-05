package io.cronx.web.model.vo.asynctask;

import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.web.model.entity.AsyncTaskDO;
import lombok.Data;

@Data
public class AsyncTaskVO {

    private long            id;

    private String          taskName;

    private AsyncTaskStatus taskStatus;

    private long            jobId;

    private Long            execOrder;

    public void convertVO(AsyncTaskDO taskDO) {
        this.id = taskDO.getId();
        this.taskName = taskDO.getTaskName();
        this.taskStatus = taskDO.getTaskStatus();
        this.jobId = taskDO.getJobId();
        this.execOrder = taskDO.getExecOrder();
    }
}
