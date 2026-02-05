package io.cronx.async.task.framework.manager;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import callback.CanNextFalseCallback;
import callback.CanNextTrueCallback;
import callback.JobPersistCallback;
import callback.TaskExpCallback;
import io.cronx.async.task.framework.alert.AlarmLevel;
import io.cronx.async.task.framework.alert.AlertService;
import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.model.AsyncJob;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.async.task.framework.model.BaseTaskContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import model.MockTaskA;
import model.MockTaskB;

@Slf4j
public class AsyncJobManagerTest2 {

    private static final TaskExpCallback      taskExpCallback      = new TaskExpCallback();

    private static final JobPersistCallback   persistCallback      = new JobPersistCallback();

    private static final CanNextFalseCallback canNextFalseCallback = new CanNextFalseCallback();

    private static final CanNextTrueCallback  canNextTrueCallback  = new CanNextTrueCallback();

    class MockAlertService implements AlertService {

        @Override
        public void sendMsg(String msg, AlarmLevel alarmLevel) {

        }

        @Override
        public String getMsgPrefix() { return null; }
    }

    @SneakyThrows
    @Test
    public void testMain() {
        AsyncJobManager asyncJobManager = new AsyncJobManager(t -> {
            log.warn("Receive exp from job manager", t);
        }, 4, () -> Lists.newArrayList());

        List<AsyncTaskBase> job1Tasks = new ArrayList<>();
        job1Tasks.add(new MockTaskA(new BaseTaskContext(1L, 1l, 1, canNextTrueCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        job1Tasks.add(new MockTaskB(new BaseTaskContext(1L, 1l, 2, canNextTrueCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));

        AsyncJob job1 = new AsyncJob(1L,
            persistCallback,
            taskExpCallback,
            job1Tasks,
            asyncJobManager.getAsyncJobCache(),
            asyncJobManager.getJobCacheLock(),
            AsyncJobStatus.WAIT_START);

        List<AsyncJob> asyncJobList = new ArrayList<>();
        asyncJobList.add(job1);

        asyncJobManager.start();
        asyncJobManager.startJobs(asyncJobList);
        Thread.currentThread().join();
    }
}
