package io.cronx.async.task.framework.manager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.model.AsyncJob;
import io.cronx.toolkit.thread.NamedThreadFactory;
import io.cronx.toolkit.utils.ExceptionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncJobManager implements AsyncJobManagerBase {

    private final AtomicBoolean            started                    = new AtomicBoolean(false);

    private ScheduledExecutorService       scheduleJobService;

    private final Consumer<Throwable>      jobMgrExpCallback;

    private ExecutorService                jobThreadPool;

    private final int                      maxPoolSize;

    @Getter
    private final Map<Long, AsyncJob>      asyncJobCache              = new LinkedHashMap<>();

    @Getter
    private final Lock                     jobCacheLock;

    private final Supplier<List<AsyncJob>> asyncJobSupplier;

    private final static String            ASYNC_JOB_SCHEDULE         = "async-job-schedule";

    private final static String            ASYNC_JOB_PARALLEL_PROCESS = "async-job-parallel-process";

    public AsyncJobManager(Consumer<Throwable> jobMgrExpCallback, int maxPoolSize, Supplier<List<AsyncJob>> asyncJobSupplier){
        this.jobMgrExpCallback = jobMgrExpCallback == null ? new DefaultJobMgrExpCallback() : jobMgrExpCallback;
        this.maxPoolSize = maxPoolSize;
        this.jobCacheLock = new ReentrantLock();
        this.asyncJobSupplier = asyncJobSupplier;
    }

    private void initScheduleJobService() {
        log.info("Start to auto schedule console job at fix rate...");
        scheduleJobService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(ASYNC_JOB_SCHEDULE));
        scheduleJobService.scheduleAtFixedRate(() -> {
            if (started.get()) {
                try {
                    addWaitStartJobsToCache();
                    processJobInParallel();
                } catch (Throwable e) {
                    log.error("Async AutoScheduler thread error.msg:" + ExceptionUtils.getRootCauseMessage(e), e);
                    jobMgrExpCallback.accept(e);
                }
            } else {
                log.warn("Job manager is stopped, nothing will be executed....");
            }
        }, 5, 5, TimeUnit.SECONDS);
        log.info("Init schedule job service success...");
    }

    private void addWaitStartJobsToCache() {
        List<AsyncJob> asyncJobs = asyncJobSupplier.get();
        asyncJobs.forEach(x -> asyncJobCache.putIfAbsent(x.getJobId(), x));
    }

    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            log.info(this.getClass().getSimpleName() + " begin to start...");

            jobThreadPool = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory(ASYNC_JOB_PARALLEL_PROCESS),
                new ThreadPoolExecutor.AbortPolicy());

            initScheduleJobService();

            log.info(this.getClass().getSimpleName() + " start successfully.");
        }
    }

    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            try {
                log.info(this.getClass().getSimpleName() + " begin to stop...");

                if (this.scheduleJobService != null) {
                    this.scheduleJobService.shutdown();
                }

                log.info(this.getClass().getSimpleName() + " stop successfully.");
            } catch (Exception e) {
                String msg = this.getClass().getSimpleName() + " stop failed,but ignore.msg:" + ExceptionUtils.getRootCauseMessage(e);
                log.error(msg, e);
            }
        }
    }

    @Override
    public void startJobs(List<AsyncJob> asyncJobList) {
        log.debug("Start async jobs, size is " + asyncJobList.size());
        for (AsyncJob asyncJob : asyncJobList) {
            if (asyncJob.getAsyncJobStatus() != AsyncJobStatus.WAIT_START) {
                continue;
            }
            AsyncJob jobInCache = asyncJobCache.get(asyncJob.getJobId());
            if (jobInCache == null) {
                asyncJobCache.put(asyncJob.getJobId(), asyncJob);
            } else {
                log.warn("Repeated add for async job " + asyncJob.getJobId());
            }
        }
    }

    @Override
    public void stopJobs(List<Long> stopJobIds) {
        log.debug("Stop async jobs, size is " + stopJobIds.size());
        for (Long jobId : stopJobIds) {
            AsyncJob jobInCache = asyncJobCache.get(jobId);
            if (jobInCache == null) {
                log.warn("No need to stop " + jobId + " in async manager, because it is not running now....");
                continue;
            }

            log.info("Stop async job in memory " + jobId);
            jobInCache.stopJob();
            jobInCache.persistSnapshot(null);
            jobInCache.removeFromCache();
        }
    }

    @Override
    public boolean checkReallyStopped(Long asyncJobId) {
        if (asyncJobCache.get(asyncJobId) == null) {
            log.info("Async job " + asyncJobId + " is really stopped, other async job status action can begin ...");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void processJobInParallel() {
        log.debug("Begin to process job in parallel, cache size is " + asyncJobCache.size());
        jobCacheLock.lock();
        try {
            for (AsyncJob asyncJob : asyncJobCache.values()) {
                if (AsyncJobStatus.WAIT_START == asyncJob.getAsyncJobStatus()) {
                    log.debug("Execute async job in thread pool, id is " + asyncJob.getJobId());
                    asyncJob.setAsyncJobStatus(AsyncJobStatus.RUNNING);
                    jobThreadPool.execute(asyncJob);
                }
            }
        } finally {
            jobCacheLock.unlock();
        }
    }

}
