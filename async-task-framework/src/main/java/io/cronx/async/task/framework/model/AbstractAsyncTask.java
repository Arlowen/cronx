package io.cronx.async.task.framework.model;

import java.util.Date;
import java.util.function.BooleanSupplier;

import io.cronx.async.task.framework.alert.AlertService;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAsyncTask<T extends BaseTaskContext> implements AsyncTaskBase {

    protected AsyncTaskStatus asyncTaskStatus;

    protected BooleanSupplier canNextSupplier;

    protected Long            jobId;

    protected Long            taskId;

    protected Date            execTime;

    protected Date            finishTime;

    protected String          errMsg;

    protected int             order;

    protected T               context;

    protected AlertService    alertService;

    @Override
    public Date getExecTime() { return execTime; }

    @Override
    public Date getFinishTime() { return finishTime; }

    @Override
    public String getErrMsg() { return errMsg; }

    @Override
    public int getExecOrder() { return order; }

    @Override
    public T getTaskContext() { return context; }

    public AbstractAsyncTask(T ctx){
        this.canNextSupplier = ctx.getCanNextSupplier();
        this.asyncTaskStatus = ctx.getAsyncTaskStatus();
        this.jobId = ctx.getJobId();
        this.taskId = ctx.getTaskId();
        this.order = ctx.getOrder();
        this.context = ctx;
        this.alertService = ctx.getAlertService();
    }

    @Override
    public AsyncTaskStatus getTaskStatus() { return asyncTaskStatus; }

    @Override
    public boolean canTriggerNext() {
        return canNextSupplier.getAsBoolean();
    }

    // ======================== jdk dynamic invoke auto fill =================

    @Override
    public void setTaskStatus(AsyncTaskStatus asyncTaskStatus) { this.asyncTaskStatus = asyncTaskStatus; }

    @Override
    public void setExecTime(Date execTime) { this.execTime = execTime; }

    @Override
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }

    @Override
    public void setErrMsg(String errMsg) { this.errMsg = errMsg; }

    @Override
    public AlertService getAlertService() { return this.alertService; }

}
