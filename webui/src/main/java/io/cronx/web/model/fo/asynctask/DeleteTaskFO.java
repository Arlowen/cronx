package io.cronx.web.model.fo.asynctask;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteTaskFO {

    @Min(value = 1, message = "{min.taskId}")
    private long taskId;
}
