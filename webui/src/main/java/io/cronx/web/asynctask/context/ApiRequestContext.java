package io.cronx.web.asynctask.context;

import java.util.function.BooleanSupplier;

import io.cronx.async.task.framework.alert.AlertService;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.model.BaseTaskContext;
import io.cronx.web.component.config.AsyncTaskConfig;
import lombok.Getter;

@Getter
public class ApiRequestContext extends BaseTaskContext {

    private final AsyncTaskConfig config;

    public ApiRequestContext(Long jobId, Long taskId, int order, BooleanSupplier canNextSupplier, AsyncTaskStatus asyncTaskStatus, AlertService alertService,
                             AsyncTaskConfig config){
        super(jobId, taskId, order, canNextSupplier, asyncTaskStatus, alertService);
        this.config = config;
    }
}
