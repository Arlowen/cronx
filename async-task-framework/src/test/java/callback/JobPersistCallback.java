package callback;

import java.util.function.Consumer;

import io.cronx.async.task.framework.model.AsyncJobExecResult;
import io.cronx.toolkit.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobPersistCallback implements Consumer<AsyncJobExecResult> {

    @Override
    public void accept(AsyncJobExecResult asyncJobExecResult) {
        log.warn("Job " + asyncJobExecResult.getJobId() + " persist result is " + JacksonUtils.toPrettyJson(asyncJobExecResult));
    }
}
