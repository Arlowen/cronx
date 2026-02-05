package model;

import io.cronx.async.task.framework.model.AbstractAsyncTask;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.async.task.framework.model.BaseTaskContext;
import io.cronx.async.task.framework.model.TaskExecContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * implements AsyncTaskBase to enable JDK dynamic proxy
 **/
@Slf4j
public class MockTaskA extends AbstractAsyncTask<BaseTaskContext> implements AsyncTaskBase {

    public MockTaskA(BaseTaskContext ctx){
        super(ctx);
    }

    @SneakyThrows
    @Override
    public void executeTask(TaskExecContext taskExecContext) {
        log.warn("Execute task with tag A parent job is " + jobId);
    }

    @Override
    public int getExecOrder() { return 0; }

    @Override
    public String getTaskLabelKey() { return null; }

}
