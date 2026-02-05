package io.cronx.async.task.framework.manager;

import java.util.List;

import io.cronx.async.task.framework.model.AsyncJob;

public interface AsyncJobManagerBase {

    void start();

    void stop();

    /**
     * Only receive not cached WAIT_START jobs. Cache jobs need to be removed and persisted then can be restarted again
     */
    void startJobs(List<AsyncJob> asyncJobList);

    /**
     * Only jobs not in end status can be stopped. Set cached job's status immediately and they will process WAIT_STOP automatically
     */
    void stopJobs(List<Long> stopJobIds);

    /**
     * Check whether the async job is really stopped
     */
    boolean checkReallyStopped(Long asyncJobId);

    /**
     * Submit to thread pool and process all WAIT_START async jobs
     */
    void processJobInParallel();

}
