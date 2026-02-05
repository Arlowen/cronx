package io.cronx.async.task.framework.model;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskExecContext {

    public TaskExecContext(AsyncJobStatus asyncJobStatus){
        this.asyncJobStatus = asyncJobStatus;
    }

    private AsyncJobStatus asyncJobStatus;

}
