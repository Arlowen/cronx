package io.cronx.async.task.framework.manager;

import java.util.ArrayList;
import java.util.Arrays;
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
import model.MockTaskC;

@Slf4j
public class AsyncJobManagerTest {

    private static TaskExpCallback      taskExpCallback      = new TaskExpCallback();

    private static JobPersistCallback   persistCallback      = new JobPersistCallback();

    private static CanNextFalseCallback canNextFalseCallback = new CanNextFalseCallback();

    private static CanNextTrueCallback  canNextTrueCallback  = new CanNextTrueCallback();

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

        // job1 is normal path
        List<AsyncJob> asyncJobList = new ArrayList<>();
        List<AsyncTaskBase> job1Tasks = new ArrayList<>();
        job1Tasks.add(new MockTaskA(new BaseTaskContext(1l, 1l, 1, canNextTrueCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        job1Tasks.add(new MockTaskB(new BaseTaskContext(1l, 1l, 2, canNextTrueCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        AsyncJob job1 = new AsyncJob(1L,
            persistCallback,
            taskExpCallback,
            job1Tasks,
            asyncJobManager.getAsyncJobCache(),
            asyncJobManager.getJobCacheLock(),
            AsyncJobStatus.WAIT_START);

        // job2 is blocked by condition
        List<AsyncTaskBase> job2Tasks = new ArrayList<>();
        job2Tasks.add(new MockTaskA(new BaseTaskContext(2l, 1l, 1, canNextFalseCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        job2Tasks.add(new MockTaskB(new BaseTaskContext(2l, 1l, 2, canNextFalseCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        AsyncJob job2 = new AsyncJob(2L,
            persistCallback,
            taskExpCallback,
            job2Tasks,
            asyncJobManager.getAsyncJobCache(),
            asyncJobManager.getJobCacheLock(),
            AsyncJobStatus.WAIT_START);

        // job3 is failed
        List<AsyncTaskBase> job3Tasks = new ArrayList<>();
        job3Tasks.add(new MockTaskC(new BaseTaskContext(3l, 1l, 1, canNextTrueCallback, AsyncTaskStatus.WAIT_START, new MockAlertService())));
        AsyncJob job3 = new AsyncJob(3L,
            persistCallback,
            taskExpCallback,
            job3Tasks,
            asyncJobManager.getAsyncJobCache(),
            asyncJobManager.getJobCacheLock(),
            AsyncJobStatus.WAIT_START);

        asyncJobList.add(job1);
        asyncJobList.add(job2);
        asyncJobList.add(job3);

        asyncJobManager.start();
        asyncJobManager.startJobs(asyncJobList);

        Thread.sleep(7000);
        // stop job2
        asyncJobManager.stopJobs(Arrays.asList(2L));

        while (job3.getAsyncJobStatus() != AsyncJobStatus.FAILED) {
            log.warn("Wait job 3 failed ,job3 current status is " + job3.getAsyncJobStatus());
            Thread.sleep(3000);
        }
        // start failed job3 again

        job3.setAsyncJobStatus(AsyncJobStatus.WAIT_START);
        asyncJobManager.startJobs(Arrays.asList(job3));

        // wait job2 stop
        while (job2.getAsyncJobStatus() != AsyncJobStatus.STOPPED) {
            log.warn("Wait job 2 stop ....");
            Thread.sleep(3000);
        }

        // start job2 again
        job2.setAsyncJobStatus(AsyncJobStatus.WAIT_START);
        asyncJobManager.startJobs(Arrays.asList(job2));

        Thread.currentThread().join();
    }
}
