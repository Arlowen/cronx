package asynctask;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.manager.AsyncJobManager;
import io.cronx.async.task.framework.model.AsyncJob;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.web.asynctask.ApiRequestTask;
import io.cronx.web.asynctask.context.ApiRequestContext;
import io.cronx.web.asynctask.trigger.AlwaysTrueTrigger;
import io.cronx.web.component.config.AsyncTaskConfig;
import io.cronx.web.constant.api.RequestMethod;
import io.cronx.web.util.JacksonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiAsyncTaskTest {

    @SneakyThrows
    @Test
    public void testMain() {
        AsyncJobManager asyncJobManager = new AsyncJobManager(t -> {
            log.warn("Receive exp from job manager", t);
        }, 4, () -> Lists.newArrayList());

        List<AsyncTaskBase> job1Tasks = new ArrayList<>();

        ApiRequestContext context = new ApiRequestContext(1L, 1L, 1, new AlwaysTrueTrigger(), AsyncTaskStatus.WAIT_START, null, createGetConfig("http://localhost:8111"));
        job1Tasks.add(new ApiRequestTask(context));

        ApiRequestContext context2 = new ApiRequestContext(1L, 2L, 2, new AlwaysTrueTrigger(), AsyncTaskStatus.WAIT_START, null, createGetConfig("http://localhost:8111"));
        job1Tasks.add(new ApiRequestTask(context2));

        AsyncJob job1 = new AsyncJob(1L,
            (t) -> log.info("Persist: " + JacksonUtils.toJsonString(t)),
            (t) -> log.info("Exception: " + JacksonUtils.toJsonString(t)),
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

    private AsyncTaskConfig createGetConfig(String host) {
        AsyncTaskConfig config = new AsyncTaskConfig();
        config.setRequestType(RequestMethod.GET);
        config.setHost(host);
        return config;
    }
}
