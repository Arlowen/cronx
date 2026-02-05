package io.cronx.async.task.framework.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.proxy.TaskExecProxy;
import io.cronx.toolkit.utils.CollectionUtils;
import io.cronx.toolkit.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *   [Thread state change]
 *   WAIT_START->RUNNING->FAILED
 *   WAIT_START->RUNNING->FINISHED
 *   WAIT_STOP->STOPPED
 *   //Occur when task execute is faster
 *   WAIT_STOP->FAILED
 *   WAIT_STOP->FINISHED
 *   [User trigger state change]
 *   WAIT_START->WAIT_STOP
 *   STOPPED->WAIT_START
 *   FAILED->WAIT_STOP
 *   FAILED->WAIT_START
 * </pre>
 **/
@Getter
@Slf4j
public class AsyncJob implements AsyncJobBase {

    private final Long                         jobId;

    private final Consumer<AsyncJobExecResult> persistCallback;

    private final Consumer<Throwable>          taskExpCallback;

    @Setter
    private AsyncJobStatus                     asyncJobStatus;

    @Getter
    @Setter
    private TaskExecContext                    taskExecContext;

    /**
     * Ordered task list
     */
    private final List<AsyncTaskBase>          asyncTasks;

    private AsyncTaskBase                      currentTask;

    private final Map<Long, AsyncJob>          mgrCachedJobs;

    /**
     * Job instance can clear cache it self, need avoid parallel access to cache that caused concurrent modification exception
     */
    private final Lock                         jobCacheLock;

    private final int                          WAIT_TRIGGER_TIME_MS = 5 * 1000;

    private Date                               startTime;

    private Date                               finishTime;

    public AsyncJob(Long jobId, Consumer<AsyncJobExecResult> persistCallback, Consumer<Throwable> taskExpCallback, List<AsyncTaskBase> asyncTasks,
                    Map<Long, AsyncJob> mgrCachedJobs, Lock jobCacheLock, AsyncJobStatus asyncJobStatus){
        checkTaskOrder(asyncTasks);
        this.jobId = jobId;
        this.persistCallback = persistCallback;
        this.taskExpCallback = taskExpCallback;
        this.asyncTasks = asyncTasks;
        this.mgrCachedJobs = mgrCachedJobs;
        this.jobCacheLock = jobCacheLock;
        this.asyncJobStatus = asyncJobStatus;
    }

    private void checkTaskOrder(List<AsyncTaskBase> asyncTasks) {
        int curOrder = -1;
        for (AsyncTaskBase asyncTaskBase : asyncTasks) {
            if (asyncTaskBase.getExecOrder() > curOrder) {
                curOrder = asyncTaskBase.getExecOrder();
            } else {
                String errMsg = "Next async task's order must greater than the previous one. Last is " + curOrder + ", current is " + asyncTaskBase.getExecOrder();
                log.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    @SneakyThrows
    @Override
    public void executeJob() {
        startTime = new Date();
        taskExecContext = new TaskExecContext(asyncJobStatus);

        for (AsyncTaskBase asyncTaskBase : asyncTasks) {
            currentTask = asyncTaskBase;
            if (asyncTaskBase.isNeedTriggerActionStatus(asyncTaskBase.getTaskStatus())) {
                try {
                    persistSnapshot(null);
                    AsyncTaskBase proxyTask = new TaskExecProxy(asyncTaskBase).createProxyObj();
                    proxyTask.executeTask(taskExecContext);
                    while (!proxyTask.canTriggerNext()) {
                        if (asyncJobStatus == AsyncJobStatus.STOPPED) {
                            log.debug("Job " + jobId + " is stopped, will exit execute job...");
                            asyncJobStatus = AsyncJobStatus.STOPPED;
                            persistSnapshot(null);
                            return;
                        } else {
                            log.debug("Task " + currentTask.getClass().getSimpleName() + " not meet condition, retry after " + WAIT_TRIGGER_TIME_MS + " ms ");
                            Thread.sleep(WAIT_TRIGGER_TIME_MS);
                        }
                    }

                    proxyTask.setTaskStatus(AsyncTaskStatus.FINISHED);
                    persistSnapshot(null);
                } catch (Exception e) {
                    String errMsg = "Async task " + asyncTaskBase.getClass().getSimpleName() + " executed failed with exception. Job id is " + jobId + ". Root cause is "
                                    + ExceptionUtils.getRootCause(e);
                    log.error(errMsg, e);
                    log.debug("Status change [ " + asyncJobStatus.name() + "->FAILED ], job id is " + jobId);
                    asyncJobStatus = AsyncJobStatus.FAILED;
                    persistSnapshot(errMsg);
                    removeFromCache();
                    taskExpCallback.accept(e);
                    throw e;
                }
            }
        }

        log.debug("Status change [ " + asyncJobStatus.name() + "->FINISHED ], job id is " + jobId);
        asyncJobStatus = AsyncJobStatus.FINISHED;
        finishTime = new Date();
        persistSnapshot(null);
        removeFromCache();
    }

    @Override
    public void stopJob() {
        this.asyncJobStatus = AsyncJobStatus.STOPPED;
        this.taskExecContext.setAsyncJobStatus(AsyncJobStatus.STOPPED);
    }

    /**
     * Execute sub task
     */
    @Override
    public void run() {
        // check the number of asyncTasks
        if (asyncJobStatus == AsyncJobStatus.RUNNING && CollectionUtils.isEmpty(asyncTasks)) {
            String errorMsg = "AsyncJob has no subtasks, it will be stopped, status change [ " + asyncJobStatus.name() + "->STOPPED ], job id is " + jobId;
            log.debug(errorMsg);
            asyncJobStatus = AsyncJobStatus.STOPPED;
            persistSnapshot(errorMsg);
            removeFromCache();
            return;
        }

        // manager will make WAIT_START -> RUNNING
        if (asyncJobStatus == AsyncJobStatus.RUNNING) {
            executeJob();
        } else {
            throw new UnsupportedOperationException("Not supported status before execute job: " + asyncJobStatus);
        }
    }

    /**
     * End status need remove instance from memory, because its lifetime is over
     */
    @Override
    public boolean isEndStatus() { return asyncJobStatus == AsyncJobStatus.STOPPED || asyncJobStatus == AsyncJobStatus.FAILED || asyncJobStatus == AsyncJobStatus.FINISHED; }

    @Override
    public void persistSnapshot(String errMsg) {
        AsyncJobExecResult asyncJobExecResult = new AsyncJobExecResult(jobId, asyncJobStatus, currentTask, errMsg, startTime, finishTime);
        persistCallback.accept(asyncJobExecResult);
    }

    @Override
    public void removeFromCache() {
        jobCacheLock.lock();
        try {
            log.debug(jobId + " remove key from cache...");
            mgrCachedJobs.remove(jobId);
        } finally {
            jobCacheLock.unlock();
        }
    }

}
