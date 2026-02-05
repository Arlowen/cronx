package io.cronx.web.service.asyncjob.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.manager.AsyncJobManager;
import io.cronx.async.task.framework.model.AsyncJob;
import io.cronx.toolkit.thread.NamedThreadFactory;
import io.cronx.toolkit.utils.CollectionUtils;
import io.cronx.toolkit.utils.ExceptionUtils;
import io.cronx.web.annotation.UnifiedPostConstruct;
import io.cronx.web.component.async.JobPersistCallback;
import io.cronx.web.mapper.AsyncJobMapper;
import io.cronx.web.mapper.AsyncTaskMapper;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.enumeration.LifeCycleState;
import io.cronx.web.model.fo.asynctask.AddTaskFO;
import io.cronx.web.model.fo.asynctask.AddTaskWithJobFO;
import io.cronx.web.model.vo.asyncjob.AsyncJobVO;
import io.cronx.web.model.vo.asyncjob.QueryJobVO;
import io.cronx.web.service.asyncjob.AsyncJobService;
import io.cronx.web.service.asynctask.AsyncTaskService;
import io.cronx.web.service.name.NamingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncJobServiceImpl implements AsyncJobService, UnifiedPostConstruct {

    private final static String          ASYNC_JOB_TIMER_SCHEDULE            = "async-job-timer-schedule";

    private final static int             ASYNC_JOB_TIMER_SCHEDULE_INIT_SEC   = 5;

    private final static int             ASYNC_JOB_TIMER_SCHEDULE_PERIOD_SEC = 5;

    /**
     * schedule finished timer jobs
     */
    private ScheduledExecutorService     timeJobScheduleService;

    private final static int             MAX_JOB_POOL_SIZE                   = 10;

    private AsyncJobManager              asyncJobManager;

    @Resource
    private AsyncTaskService             taskService;

    @Resource
    private NamingService                namingService;

    @Resource
    private AsyncJobMapper               asyncJobMapper;

    @Resource
    private AsyncTaskMapper              asyncTaskMapper;

    @Resource
    private PlatformTransactionManager   platformTransactionManager;

    @Resource
    private DefaultTransactionDefinition defaultTransactionDefinition;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createJob(String jobDesc) {
        AsyncJobDO jobDO = new AsyncJobDO();
        jobDO.setJobName(namingService.genJobName());
        jobDO.setJobDesc(jobDesc);
        jobDO.setJobStatus(AsyncJobStatus.STOPPED);
        jobDO.setLifeCycleState(LifeCycleState.CREATED);
        asyncJobMapper.insert(jobDO);

        //  if (CollectionUtils.isEmpty(taskFOList)) {
        //      return;
        //  }
        //  List<AddTaskFO> taskFOS = taskFOList.stream().map(taskFO -> taskFO.convertAddTaskFO(jobId)).collect(Collectors.toList());
        //  taskService.addTasks(taskFOS);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByJobId(long jobId) {
        AsyncJobDO jobDO = asyncJobMapper.queryJobById(jobId);

        if (jobDO == null) {
            throw new IllegalArgumentException("AsyncJob not founded, jobId: " + jobId);
        }

        if (jobDO.getJobStatus() == AsyncJobStatus.RUNNING) {
            throw new IllegalStateException("AsyncJob is RUNNING, stop it first");
        }

        // TODO need to check taskList status

        asyncJobMapper.deleteByJobId(jobId);

        taskService.deleteByJobId(jobId);

    }

    @Override
    public QueryJobVO listJobPageByCondition(long pageSize, long pageNum) {
        long offset = (pageNum - 1) * pageSize;
        List<AsyncJobDO> jobDOS = asyncJobMapper.queryByCondition(offset, pageSize);

        if (jobDOS.isEmpty()) {
            return new QueryJobVO(new ArrayList<>(), 0L);
        }

        List<AsyncJobVO> asyncJobVOS = jobDOS.stream().map(jobDO -> {
            AsyncJobVO asyncJobVO = new AsyncJobVO();
            asyncJobVO.convertVO(jobDO);
            return asyncJobVO;
        }).collect(Collectors.toList());

        Integer totalCount = asyncJobMapper.queryCountByCondition();
        return new QueryJobVO(asyncJobVOS, totalCount);
    }

    @Override
    public void init() {
        log.info(this.getClass().getSimpleName() + " begin to start...");

        asyncJobManager = new AsyncJobManager(t -> {
            log.warn("Receive exp from job manager", t);
        }, MAX_JOB_POOL_SIZE, this::fetchWaitStartJobs);

        startTimerJobScheduler();

        refreshLastRunningJobs();

        asyncJobManager.start();

        log.info(this.getClass().getSimpleName() + " start successfully.");
    }

    @PreDestroy
    public void stop() {
        log.info(this.getClass().getSimpleName() + " begin to stop...");

        if (timeJobScheduleService != null) {
            timeJobScheduleService.shutdown();
        }

        if (asyncJobManager != null) {
            asyncJobManager.stop();
        }

        log.info(this.getClass().getSimpleName() + " stop successfully.");
    }

    private void startTimerJobScheduler() {
        timeJobScheduleService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(ASYNC_JOB_TIMER_SCHEDULE));
        log.info("Try start timer jobs by update their status...");
        timeJobScheduleService.scheduleAtFixedRate(() -> {
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(defaultTransactionDefinition);
            try {
                List<AsyncJobDO> needStartTimerJobs = asyncJobMapper.queryNeedStartTimerAsyncJobs(new Date());
                List<Long> needStartJobIds = needStartTimerJobs.stream().map(AsyncJobDO::getId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(needStartTimerJobs)) {
                    platformTransactionManager.commit(transactionStatus);
                    return;
                }

                asyncJobMapper.startTimerFinishedJobs(new Date());
                asyncTaskMapper.resetTaskStatusToWaitStartBatch(needStartJobIds);
                platformTransactionManager.commit(transactionStatus);
            } catch (Exception e) {
                String errMsg = "Start timer job failed with exception. Root cause is " + ExceptionUtils.getRootCauseMessage(e);
                log.error(errMsg, e);
                platformTransactionManager.rollback(transactionStatus);
            }
        }, ASYNC_JOB_TIMER_SCHEDULE_INIT_SEC, ASYNC_JOB_TIMER_SCHEDULE_PERIOD_SEC, TimeUnit.SECONDS);
        log.info("Init timer schedule job service success.");
    }

    private void refreshLastRunningJobs() {
        List<AsyncJobDO> lastRunningJobs = asyncJobMapper.queryAsyncJobsByStatus(AsyncJobStatus.RUNNING);
        List<Long> refreshIds = lastRunningJobs.stream().map(AsyncJobDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(refreshIds)) {
            // only update job status to support subtask continue from last step....
            asyncJobMapper.updateStatusBatch(AsyncJobStatus.WAIT_START, refreshIds);
        }
        log.info("Finish reset async job status after console restart, reset job size is "
                 + (CollectionUtils.isEmpty(lastRunningJobs) ? "0" : String.valueOf(lastRunningJobs.size())));
    }

    private List<AsyncJob> fetchWaitStartJobs() {
        List<AsyncJobDO> jobs = asyncJobMapper.queryAsyncJobsByStatus(AsyncJobStatus.WAIT_START);
        if (CollectionUtils.isNotEmpty(jobs)) {
            log.info("Fetch total WAIT_START job count " + jobs.size());
        }
        return jobs.stream().map(this::genAsyncJobFromDO).collect(Collectors.toList());
    }

    @Override
    public AsyncJobManager getAsyncJobManager() { return asyncJobManager; }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void startAsyncJob(Long jobId) {
        AsyncJobDO asyncJobDO = asyncJobMapper.queryJobById(jobId);

        if (asyncJobDO == null) {
            throw new IllegalArgumentException("AsyncJob not founded, jobId: " + jobId);
        }
        // todo

        asyncJobMapper.updateStatus(AsyncJobStatus.WAIT_START.name(), asyncJobDO.getId());
        AsyncJob asyncJob = genAsyncJobFromDO(asyncJobDO);
        asyncJobManager.startJobs(Collections.singletonList(asyncJob));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void stopAsyncJob(Long jobId) {
        AsyncJobDO asyncJobDO = asyncJobMapper.queryJobById(jobId);

        if (asyncJobDO == null) {
            throw new IllegalArgumentException("AsyncJob not founded, jobId: " + jobId);
        }
        // todo

        asyncJobMapper.updateStatus(AsyncJobStatus.STOPPED.name(), asyncJobDO.getId());
        asyncJobManager.stopJobs(Collections.singletonList(asyncJobDO.getId()));
    }

    @Override
    public AsyncJob genAsyncJobFromDO(AsyncJobDO asyncJobDO) {
        // TODO taskExpCallback
        return new AsyncJob(asyncJobDO.getId(),
            new JobPersistCallback(asyncJobMapper, asyncTaskMapper),
            t -> log.error("Current async task executed failed with exception. Root cause is " + ExceptionUtils.getRootCause(t)),
            taskService.assembleTasksFromDb(asyncJobDO, false),
            asyncJobManager.getAsyncJobCache(),
            asyncJobManager.getJobCacheLock(),
            AsyncJobStatus.WAIT_START);
    }
}
