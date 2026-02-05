package io.cronx.web.model.fo.asynctask;

import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.cronx.web.constant.api.RequestMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTaskFO {

    @Min(value = 1, message = "{min.jobid}")
    private Long                jobId;

    @NotNull(message = "{notnull.execorder}")
    private Long                execOrder;

    @Min(value = 1, message = "{min.apisourceid}")
    private Long                apiSourceId;

    // only used for gen core config
    private Long                taskId;

    private String              taskDesc;

    /**
     * for custom api in task
     */
    private String              host;

    private RequestMethod       requestType;

    private Map<String, Object> cookies;

    private Map<String, Object> params;

    private String              body;

}
