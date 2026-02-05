package io.cronx.async.task.framework.model;

import java.util.function.BooleanSupplier;

import io.cronx.async.task.framework.alert.AlertService;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseTaskContext {

    private Long            jobId;

    private Long            taskId;

    private int             order;

    private BooleanSupplier canNextSupplier;

    private AsyncTaskStatus asyncTaskStatus;

    private AlertService    alertService;

    public BaseTaskContext(Long jobId, Long taskId, int order, BooleanSupplier canNextSupplier, AsyncTaskStatus asyncTaskStatus, AlertService alertService){
        this.jobId = jobId;
        this.taskId = taskId;
        this.order = order;
        this.canNextSupplier = canNextSupplier;
        this.asyncTaskStatus = asyncTaskStatus;
        this.alertService = alertService;
    }
}
