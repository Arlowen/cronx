package io.cronx.web.model.fo.asynctask;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.cronx.toolkit.utils.CollectionUtils;
import io.cronx.web.constant.api.RequestMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTaskWithJobFO {

    @Min(value = 1, message = "{min.apisourceid}")
    private long                apiSourceId;

    private RequestMethod       requestType;

    private Map<String, Object> cookies;

    private Map<String, Object> params;

    private String              body;

    private String              taskDesc;

    @NotNull(message = "{notnull.execorder}")
    private Long                execOrder;

    public AddTaskFO convertAddTaskFO(long jobId) {
        AddTaskFO addTaskFO = new AddTaskFO();
        addTaskFO.setJobId(jobId);
        addTaskFO.setApiSourceId(this.apiSourceId);
        addTaskFO.setRequestType(this.requestType);
        addTaskFO.setCookies(CollectionUtils.isEmpty(this.cookies) ? new HashMap<>() : new HashMap<>(this.cookies));
        addTaskFO.setParams(CollectionUtils.isEmpty(this.params) ? new HashMap<>() : new HashMap<>(this.params));
        addTaskFO.setBody(this.body);
        addTaskFO.setTaskDesc(this.taskDesc);
        addTaskFO.setExecOrder(this.execOrder);
        return addTaskFO;
    }
}
