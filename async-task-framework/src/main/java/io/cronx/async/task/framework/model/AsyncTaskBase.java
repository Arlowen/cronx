package io.cronx.async.task.framework.model;

import java.util.Date;

import io.cronx.async.task.framework.alert.AlertService;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.model.annotation.TaskAutoProcess;

/**
 * <pre>
 *   Design:
 *   - Every async task is dependent and not relationship. If you want to have relation, just keep them in trigger. This make async task simple
 *   - Condition trigger and async task is loose coupled
 *   - Task and trigger can compose custom
 * </pre>
 **/
public interface AsyncTaskBase<T extends BaseTaskContext> {

    /**
     * WAIT_START and RUNNING all be seen need trigger execute
     */
    default boolean isNeedTriggerActionStatus(AsyncTaskStatus asyncTaskStatus) {
        return AsyncTaskStatus.WAIT_START == asyncTaskStatus || AsyncTaskStatus.RUNNING == asyncTaskStatus || AsyncTaskStatus.ABNORMAL == asyncTaskStatus;
    }

    /**
     * Execute current task. @TaskAutoStatus enable auto manager
     *
     * @return true means normal exit, false means stop task by signal
     */
    @TaskAutoProcess
    void executeTask(TaskExecContext taskExecContext);

    AsyncTaskStatus getTaskStatus();

    boolean canTriggerNext();

    /**
     * task order start from 1. Tasks in a job can be organized by order
     */
    int getExecOrder();

    Date getExecTime();

    Date getFinishTime();

    String getErrMsg();

    /**
     * Task context can be store runtime info
     */
    T getTaskContext();

    /**
     * Label key can used for i18n and indicate what the task do
     *
     * @return
     */
    String getTaskLabelKey();

    // ================= jdk dynamic invoke will auto fill =========

    void setTaskStatus(AsyncTaskStatus asyncTaskStatus);

    void setExecTime(Date execTime);

    void setFinishTime(Date finishTime);

    void setErrMsg(String errMsg);

    AlertService getAlertService();

}
