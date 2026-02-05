package model;

import io.cronx.async.task.framework.model.AbstractAsyncTask;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.async.task.framework.model.BaseTaskContext;
import io.cronx.async.task.framework.model.TaskExecContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockTaskC extends AbstractAsyncTask<BaseTaskContext> implements AsyncTaskBase {

    public MockTaskC(BaseTaskContext ctx){
        super(ctx);
    }

    @SneakyThrows
    @Override
    public void executeTask(TaskExecContext taskExecContext) {
        log.warn("Execute task C (mock exception)...... parent job id is " + jobId);
        throw new RuntimeException("Mocked exception...");
    }

    @Override
    public int getExecOrder() { return 3; }

    @Override
    public String getTaskLabelKey() { return null; }

}
