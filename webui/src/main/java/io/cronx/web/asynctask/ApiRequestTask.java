package io.cronx.web.asynctask;

import io.cronx.async.task.framework.model.AbstractAsyncTask;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.async.task.framework.model.TaskExecContext;
import io.cronx.toolkit.http.CronxHttpClient;
import io.cronx.toolkit.model.ResponseData;
import io.cronx.toolkit.utils.JacksonUtils;
import io.cronx.web.asynctask.context.ApiRequestContext;
import io.cronx.web.component.config.AsyncTaskConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiRequestTask extends AbstractAsyncTask<ApiRequestContext> implements AsyncTaskBase {

    public ApiRequestTask(ApiRequestContext ctx){
        super(ctx);
    }

    @Override
    public void executeTask(TaskExecContext taskExecContext) {
        log.info("Start ApiRequestTask, jobId: " + jobId);
        ApiRequestContext taskContext = getTaskContext();
        AsyncTaskConfig config = taskContext.getConfig();
        switch (config.getRequestType()) {
            case POST: {
                ResponseData responseData = CronxHttpClient.post(config.getHost(), JacksonUtils.toJsonString(config.getParams()), config.getHeader());
                log.info("Request Res: " + JacksonUtils.toJsonString(responseData));
                break;
            }
            case GET: {
                ResponseData responseData = CronxHttpClient.getWithResponseData(config.getHost(), config.getHeader());
                log.info("Request Res: " + JacksonUtils.toJsonString(responseData));
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported RequestType: " + config.getRequestType());
            }
        }
        log.info("Finish ApiRequestTask, jobId: " + jobId);
    }

    @Override
    public String getTaskLabelKey() { return null; }

}
